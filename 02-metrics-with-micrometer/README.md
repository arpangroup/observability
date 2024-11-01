<style>
red { color: Red; font-style: bold}
orange { color: Orange }
green { color: Green }

</style>



# 1. [SpringBoot Micrometer - A `Facade` over `actuator` for  Vendor Neutral Monitorig](https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector)
**Actuator:** Device for converting energy(often electrical, hydraulic etc.) into motion.<br/>
**Micrometer:** Instrument for precise measurement of small distances.

### Micrometer is used by Spring Boot Actuator to expose application metrics because it offers several benefits, including:

- **Monitoring system switching:** 
  <br/>Micrometer allows developers to switch between monitoring systems without changing their code.
    
- **Rich metric collection:** 
  <br/>Micrometer provides a wide range of metric types, including gauges, counters, timers, and distribution summaries.
- **Conditional metric collection:** 
  <br/>Micrometer's facade design allows developers to dynamically turn metrics on or off based on the deployment environment.


 - **Think of it like SLF4J, but for metrics** - a simple `facade` to integrate actuator metrics with external monitoring systems.
 - Micrometer is a <red>dimensional-first</red> metrics collection facade A metrics collection library that provides a facade over various monitoring systems.
 - Micrometer is used by Actuator under the hood **to expose metrics about your application**.
 - The main benefit of using Micrometer is that **it allows you to switch between monitoring systems without changing your code**
 - **Spring Framework 6 and Spring Boot 3:** comes with complete implementation of actuator/micrometer library

### 1.1. Key Differences:
| Logging Concepts                                                                                                                                                         | Micrometer                                                                                                                                                                           |
|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Focuses on providing endpoints for monitoring and managing the Spring Boot application itself.                                                                           | Focuses on collecting and exporting metrics data to external monitoring systems, often integrated with Actuator for more detailed metrics.                                           |
| **In short**, Actuator provides the management endpoints,                                                                                                                | **while** Micrometer provides the underlying metrics collection and export.                                                                                                          |
| **Purpose:** Provides production-ready features to help monitor and manage a Spring Boot application.                                                                    | **Purpose:** A metrics collection library that provides a facade over various monitoring systems.                                                                                    |
| **Features:** Offers various endpoints (like `/actuator/health`, `/actuator/metrics`) that expose information about the application's health, metrics, environment, etc. | **Features:** Supports multiple monitoring systems (like Prometheus, Datadog, New Relic, AWS CloudWatch etc.) and allows you to define custom metrics, timers, counters, and gauges. |

### 1.2. What do I get out of the box?
Spring Boot 2 autoconfigures quite a few metrics for you, including:
 - JVM, report utilization of:
 - CPU usage
 - Spring MVC and WebFlux request latencies
 - RestTemplate latencies
 - Cache utilization
 - Datasource utilization, including HikariCP pool metrics
 - Logback: record the number of events logged to Logback at each level
 - Uptime: report a gauge for uptime and a fixed gauge representing the application’s absolute start time
 - Tomcat usage

### 1.3. Micrometer - Logging Similarities:
| Logging Concepts      | ==> | Micrometer Equivalents |
|-----------------------|-----|------------------------|
| Facade                | ==> | MeterRegistry          |
| Muting                | ==> | MeterRegistry          |
| Multiple Destinations | ==> | Composite Registry     |
| Common Metadata       | ==> | Common Tags            |


### 1.4. [The importance of dimensionality](https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector#the-importance-of-dimensionality)
Spring Boot 1’s metrics interfaces were hierarchical in nature. This means that published metrics were identified entirely by their name. So you might have a metric named `jvm.memory.used`.
<br/><br/>
When you’re looking at metrics from a single application instance, this seems suitable. But what if you have 10 instances all publishing `jvm.memory.used` to the same monitoring system? How do we distinguish between them in the event that memory consumption spikes unexpectedly on one instance?
<br/><br/>
The answer is generally to add to the name, for example by adding a prefix or suffix to the name. So we might change the name to `${HOST}.jvm.memory.used`, where we substitute ${HOST} for the host name. After redeploying all 10 instances, we now can identify which instance is under memory pressure. And in a typical hierarchical monitoring system, we can reason about the sum of memory used across all instances by wildcarding the name somehow:
````
${HOST}.jvm.memory.used
${REGION}.${HOST}.jvm.memory.used
````
The same metric in Micrometer would have been recorded with tags (a.k.a. dimensions):
````java
Gauge.builder("jvm.memory.used", ..)
  .tag("host", "MYHOST")
  .tag("region", "us-east-1")
  .register(registry);
````

## 1.5. [Meter filters](https://spring.io/blog/2018/03/16/micrometer-spring-boot-2-s-new-application-metrics-collector#meter-filters)
Meter filters allow you to control how and when meters are registered and what kinds of statistics they emit. Meter filters serve three basic functions:
 - **Deny** (or accept) meters from being registered.
 - **Transform** meter IDs (e.g. changing the name, adding or removing tags, changing description or base units).
 - **Configure** distribution statistics for some meter types (e.g. percentiles, histograms, SLAs for timers and distribution summaries).
````properties
management.metrics.enable.jvm=false
management.metrics.distribution.percentiles-histogram.http.server.requests=true
management.metrics.distribution.sla.http.server.requests=1ms,5ms
````

````java
// This filter adds a common tag with the application name to all metrics, 
// which is helpful when you’re monitoring multiple applications and want to distinguish between them.
@Bean
MeterFilter commonTagsMeterFilter() {
    return MeterFilter.commonTags(Arrays.asList(Tag.of("application", "my-spring-boot-app")));
}
````

````java
// Remove tags if sensitive/not-needed.
@Configuration
class MeterConfig {
    public MeterFilter ignoreTagsFilter() {
        return MeterFilter.ignoreTags("tag1", "tag2");
    }
}
````

````java
// Adding tags if required for specific metric.
@Configuration
class MeterConfig {
    public MeterFilter transformMeter() {
        return new MeterFilter() {
            @Override
            public Meter.Id map(Meter.Id id) {
                if (List.of("metricname").contains(id.getName())) {
                    // adding custom tag into metrics
                    List<Tag> tags = List.of(Tag.of("newTagName", "newTagValue"));
                    id = id.withTags(tags);
                }
                return id;
            }
        };
    }
}
````




# 2. Creating Custom Metrics
| Metric Type   | Description                                                                       | Example                    |
|---------------|-----------------------------------------------------------------------------------|----------------------------|
| **Counter**   | Record a value that only goes `up` <br/>Query how fast the value is increasing    | http.server.requests.count |
| **Gauge**     | Record a value that only goes `up` & `down` <br/>You don't need to query its rate | |
| **Timer**     | Measure the duration of events and the rate at which they occur.                  | |
| **Histogram** | Take many measurement of a value to later calculate average or percentile         | |

## 2.1. Counter: 
- Query how fast the value is increasing
- Ex: No. of request served, Task Completed


<br/>Let’s start with the counter - metric `api_orders_get` will tell us how many times endpoint /api/orders has been called. 
We will use tags concept to group them by userId (if it has been provided).

````java
public class OrderController {
    private final MeterRegistry meterRegistry;
    private final OrderService orderService;

    @GetMapping("/orders")
    public List<Order> getOrders(@RequestParam(required = false) String userId) {
        Counter orderCounter = Counter.builder("api_orders_get ")
                .description("a number of requests to /api/orders endpoint")
                .tags("region", "us-east")
                .tags("userId", StringUtils.isEmpty(title) ? "all" : userId)
                .register(registry);
        orderCounter.increment();
        
        return orderService.getAllOrders();
    }
}
````

## 2.2. Gauge:
- Record a value that only goes `up` & `down`
- You don't need to query its rate
- Ex: Temperature, speed, memory usage


<br/>Now, in OrderService we will use two metrics types. 
Firstly I have defined `items_count` **Gauge** to answer the question “how much items do we have in stock”. You can image that it may represent current store’s offer.

I also used `service_orders_find` **Timer** to measure how long does it take to find books by title.

````java
public class OrderService {
    private final MeterRegistry meterRegistry;
    private final OrderRepository orderRepository;

    public OrderService() {
        Gauge.builder("items_count", booksRepository::countBooks)
                .description("A current number of items in the inventory")
                .register(meterRegistry);
    }

    public List<Order> findByOrderId(String orderId) {
        Tag orderTag = Tag.of("orderId", StringUtils.isEmpty(title) ? "all" : orderId);


        Timer.Sample timer = Timer.start(meterRegistry);

        List<Order> orders = booksRepository.getBooks();
        Thread.sleep(ThreadLocalRandom.current().nextInt(200, 400));

        timer.stop(Timer.builder("service_orders_find")
                .description("orders searching timer")
                .tags(List.of(orderTag))
                .register(meterRegistry));

        return orders;
    }
}
````

## 2.3. Timer:
- Query how fast the value is i`ncreasing
- Ex: No. of request served, 
````java
public class MyController {

    private final Timer myTimer;

    public MyController(MeterRegistry registry) {
        myTimer = Timer.builder("my.timer")
                .description("Times something")
                .tags("region", "us-east")
                .register(registry);
    }

    @GetMapping("/time")
    public void timeSomething() {
        myTimer.record(() -> {
            // perform task to be timed
        });
    }
}
````

## 2.4. Histogram:
- Query how fast the value is increasing
- Ex: No. of request served,


| Request | ResponseTime |
|---------|--------------|
| R1      | 0.23         |
| R2      | 3.01         |
| R3      | 2.87         |
| R4      | 1.11         |
| R5      | 1.5          |
| R6      | 1.99         |
| R7      | 7.43         |
| R8      | 19.99        |
| R9      | 30.33        |
| R10     | 6.18         |

<br/>
<br/>How many request took 5-10s to respond?

| Range / <br/>Bucket | Count                           |
|---------------------|---------------------------------|
| <= 1                | 0 --> 1                         |
| <= 2                | 0 --> 1                         |
| <= 3                | 0 --> 1 --> 2 --> 3 -------> 5  |
| <= 4                | 0 --> 1 --> 2 --> 3 -------> 6  |
| <= 5                | 0 --> 1 --> 2 --> 3 -------> 6  |
| <= 6                | 0 --> 1 --> 2 --> 3 -------> 6  |
| <= 7                | 0 --> 1 --> 2 --> 3 -------> 7  | 
| ... <br/> ...       | ... <br/> ...                   | 
| <= 20               | 0 --> 1 --> 2 --> 3 -------> 9  | 
| <= 30               | 0 --> 1 --> 2 --> 3 -------> 9  | 
| <= +INF             | 0 --> 1 --> 2 --> 3 -------> 10 | 


````yml
management:
  metrics:
    distribution:
      percentile-histogram.http.server.requests=true
      slo.http.server.requests=50ms, 100ms, 200ms, 400ms
      percentile.http.server.requests=0.5, 0.9, 0.95, 0.99, 0.999
````


## 2.5. Summary:

| Request | ResponseTime    |
|---------|-----------------|
| R1      | 0.23            |
| R2      | 0.3             |
| R3      | 0.19            |
| R4      | 0.29            |
| R5      | 0.4             |
| R6      | 0.36            |
| R7      | 0.41            |
| R8      | 0.26            |
| R9      | <red>60.33<red> |
| R10     | 0.5             |

Avg. ResponseTime
    <br>&nbsp; = (0.23 + 0.3 + 0.19 + 0.29 + 0.4 + 0.36 + 0.41 + 0.26 + 60.33 + 0.5)
    <br>&nbsp; = <red>6.807</red>  &nbsp; <--wrong calculation, because almost 9 request took place before 0.5sec

> Just because of one single request (R9) we are analyzing the performance in a wrong way

> To ignore this **outlier**, we use **percentile**

### 50th Percentile:
1. **Sort the data point:** 0.19, 0.23, 0.26, 0.29, 0.3, 0.36, 0.4, 0.41, 0.5, 60.33
2. **Take 2 middle value & average:** (0.3 + 0.36) / 2 = 0.33 <br/>
**i.e., 50 % of request took 0.33sec**.



## 3. Conditional Meter Registration
Sometimes, you might want to register certain meters only if certain conditions are met. You can achieve this by implementing a custom `MeterRegistryCustomizer`.
````java
// This customization allows you to conditionally register metrics based on the application state or configuration.
@Bean
MeterRegistryCustomizer<MeterRegistry> conditionalMetrics() {
    return registry -> {
        if (shouldBeRegistered()) {
            MeterBinder binder = ... // create your meter binder
            binder.bindTo(registry);
        }
    };
}

private boolean shouldBeRegistered() {
    // logic to determine if the meter should be registered
}
````

## 4. Custom Meter Binders
For more complex scenarios, you can create custom meter binders. A `MeterBinder` is a component that encapsulates the logic for registering meters.
````java
//This example shows how you can encapsulate the registration of a custom gauge within a MeterBinder.
public class MyCustomMetrics implements MeterBinder {

    private final MyService service;

    public MyCustomMetrics(MyService service) {
        this.service = service;
    }

    @Override
    public void bindTo(MeterRegistry registry) {
        Gauge.builder("custom.gauge", service, MyService::getValue)
                .description("A custom gauge metric")
                .register(registry);
    }
}
````

# 5. FAQ 

## 5.1. [What is "High Cardinality"?](https://develotters.com/posts/high-cardinality/)
In mathematics, [cardinality](https://en.wikipedia.org/wiki/Cardinality) is the measure of the number of elements (distinct values) of a set. For example, the set `{200,404,503}` contains 3 elements so the cardinality of it is 3. But what does “High Cardinality” have to do with (time series) metrics? Glad you asked, the answer is everything. :)

## HTTP Example
Let’s say you want to count the incoming HTTP requests to your web service so that you are aware of the traffic patterns/throughput. If you create a `Counter` and increment it when your service receives an incoming request, that would look something like this ([Micrometer](https://micrometer.io/docs/concepts) example):

````java
Counter.builder("http.requests")
    .register(registry)
    .increment();
````

This will result in one time series (Prometheus output):
````
http_requests_total 10.0
````
With this, you can see the number of incoming HTTP requests (10). The cardinality of this metric is 1 since you have one time series.

## Increasing Cardinality
This is great but you might want more, for example tracking the traffic patterns/throughput for the different [HTTP methods](https://en.wikipedia.org/wiki/HTTP#Request_methods) (e.g.: `GET` vs. `POST`). This is what dimensions (or tags) are for. Dimensions let you slice your metrics and drill down along them. This means that if you add an HTTP method dimension (tag) you can track the individual methods or any combinations of them, e.g.:
- How many `GET` requests did the service receive?
- How many `POST` requests did the service receive?
- How many `POST` + `DELETE` requests did the service receive?
- How many requests did the service receive?

<br/>Doing this would look something like this (Micrometer example):
````java
Counter.builder("http.requests")
    .tag("method", method)
    .register(registry)
    .increment();
````
Let’s say `GET`, `POST`, `PUT`, and `DELETE` are supported and everything else will be mapped to UNSUPPORTED by your service. With this behavior, doing the above will result in 5 time series (Prometheus output):
````
http_requests_total{method="GET",} 3.0
http_requests_total{method="POST",} 2.0
http_requests_total{method="PUT",} 1.0
http_requests_total{method="DELETE",} 1.0
http_requests_total{method="UNSUPPORTED",} 3.0
````
Now you can track the number of incoming HTTP requests by HTTP methods. The cardinality of this metric is 5 since you have 5 time series (5 different possible HTTP methods).

## Further Increasing Cardinality
### What if you also want to track the status codes?
````java
Counter.builder("http.requests")
    .tag("method", method)
    .tag("status", status)
    .register(registry)
    .increment();
````
Since there are quite a few status codes, you can have much more time series:
````
http_requests_total{method="GET",status="200",} 1.0
http_requests_total{method="GET",status="404",} 1.0
http_requests_total{method="GET",status="500",} 1.0
http_requests_total{method="POST",status="201",} 1.0
http_requests_total{method="POST",status="403",} 1.0
http_requests_total{method="PUT",status="503",} 1.0
http_requests_total{method="DELETE",status="204",} 1.0
http_requests_total{method="UNSUPPORTED",status="501",} 3.0
````
Not every combination is valid but let’s say any `GET`, `POST`, `PUT`, and `DELETE` call can result in one of 50 status codes and `UNSUPPORTED` is always 501. So you can have `50 * 4 + 1 = 201` time series.


## Exploding Cardinality
Let’s also add the name of the endpoints that this service can handle and let’s say there are 10 of them (`users`, `cars`, etc.); doing this will bring cardinality up to `201 * 10 = 2010`.

Is this a problem? That’s a good question and you might know the answer is… It depends.

Your time series DB (metrics backend) and your metrics library can handle way more time series than you might think (except if you are very low on memory and/or disk). But high cardinality can be a problem if you have a practically unbounded dimension. Let’s see an example of that. Let’s say instead of the name of the endpoint you add the URI: `/users/123`, `/cars/456`, etc. If you have a million users and 1000 cars doing this will mean a one-billion multiplier in your time series on top of the cardinality that you already have.


## High Cardinality
But the situation is somewhat worse than this. Since the URI is user input, this practically means infinite instead of the one-billion multiplier since users can generate HTTP requests with a random number generator until your service runs out of memory. This is usually what we mean by high cardinality: a lot of data is ok but infinite data will cause problems since you cannot store an endless amount of data in non-infinite space, either your service or your metrics backend will suffer.

### Please always normalize user data and be aware of the cardinality of non-user data!**


## 5.2. [Should you use Java Agents to instrument your application?](https://develotters.com/posts/should-you-use-java-agents-to-instrument-your-application/)
Trade-offs around Java agent-based instrumentation

If you have worked with any APMs (or other monitoring/observability products) under Java, you might have heard something like: `[paraphrased-marketing-text]` to track everything from performance issues to errors within your application, just attach the Java Agent to your app and all of your problems are gone `[/paraphrased-marketing-text]`.

You can read things like this in product docs, or blog posts or hear from sales, marketing, and even in conference talks. What I think you can hear less are the trade-offs, so let’s dive a little bit into that.

Let’s start with the good news: agents are relatively simple to adopt. If you want to observe your application, attaching a Java Agent is usually not too complicated: you “just” need to copy the agent `.jar` to your environment (e.g.: prod) and let the JVM know about it in a runtime argument. So you need to deploy and manage your app and the Java Agent but that’s pretty much it, it’s simple. Unfortunately, that’s the only advantage I can think of but let me know if you know others.

But what are the `disadvantages`? Well, let’s look into the not-so-great parts of using Java Agents to instrument your application.

### Lack of GraalVM native-image support:
### Performance:
This is just theory, I have never measured it but theoretically, agent-based instrumentation that uses a lot of reflection might not be as performant as “hardcoded” instrumentation (the instrumentation is part of the codebase) because in general reflection is usually slower than doing the same thing in a “hardcoded” way.


