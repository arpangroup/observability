# [Observability With Spring Boot 3](https://www.baeldung.com/spring-boot-3-observability)
[spring Docs](https://docs.spring.io/spring-boot/reference/actuator/observability.html)

Observability is the ability to measure the internal state of a system only by its external outputs (logs, metrics, and traces). We can learn about the basics in “Observability in Distributed Systems.”

## 1. Observation and ObservationRegistry
````java
ObservationRegistry observationRegistry = ObservationRegistry.create();
Observation observation = Observation.createNotStarted("sample", observationRegistry);
````

<br/>We can use the Observation type like this:
````java
@Component
public class MyCustomObservation {

	private final ObservationRegistry observationRegistry;

	public MyCustomObservation(ObservationRegistry observationRegistry) {
		this.observationRegistry = observationRegistry;
	}

	public void doSomething() {
		Observation.createNotStarted("doSomething", this.observationRegistry)
			.lowCardinalityKeyValue("locale", "en-US")
			.highCardinalityKeyValue("userId", "42")
			.observe(() -> {
				// Execute business logic here
			});
	}

}
````

## 1.1. Preventing Observations
````properties
management.observations.enable.denied.prefix=false
management.observations.enable.another.denied.prefix=false
````


## 2. ObservationHandler
````java
@Sl4J
public class SimpleLoggingHandler implements ObservationHandler<Observation.Context> {

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }

    @Override
    public void onStart(Observation.Context context) {
        log.info("Starting");
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        log.info("Scope opened");
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        log.info("Scope closed");
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("Stopping");
    }

    @Override
    public void onError(Observation.Context context) {
        log.info("Error");
    }
}
````

We then register the ObservationHandler at the ObservationRegistry before creating the Observation:

````java
import io.micrometer.observation.ObservationRegistry;
import org.springframework.context.annotation.Bean;

@Configuration
public class ObservationConfig {

    @Bean
    public void setup(ObservationRegistry observationRegistry) {
        observationRegistry
                .observationConfig()
                .observationHandler(new SimpleLoggingHandler());
    }
}
````



## 3. AOP
> + spring-boot-starter-aop
````java
@Configuration
public class ObservedAspectConfiguration {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }
}
````

````java
@Observed(name = "greetingService")
@Service
public class GreetingService {

    public String sayHello() {
        return "Hello World!";
    }
}
````

curl -X GET http://localhost:8080/actuator/metrics/greetings
````json
{
    "name": "greetingService",
    "baseUnit": "seconds",
    "measurements": [
        {
            "statistic": "COUNT",
            "value": 15
        },
        {
            "statistic": "TOTAL_TIME",
            "value": 0.0237577
        },
        {
            "statistic": "MAX",
            "value": 0.0035475
        }
    ],
    ...
}
````
# 5. [Micrometer Tracing](https://micrometer.io/docs/tracing)
The former Spring Cloud Sleuth project has moved to Micrometer, the core to Micrometer Tracing since Spring Boot 3. We can find the definition of Micrometer Tracing in the documentation:

> Micrometer Tracing provides a simple facade for the most popular tracer libraries, letting you instrument your JVM-based application code without vendor lock-in. It is designed to add little to no overhead to your tracing collection activity while maximizing the portability of your tracing effort.

We can use it standalone, but it also integrates with the Observation API by providing ObservationHandler extensions.

## 5.1. Integration Into Observation API
To use Micrometer Tracing, we need to add the following dependency to our project. The version is managed by Spring Boot:

````xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing</artifactId>
</dependency>
````
Next, we need one of the supported tracers (currently OpenZipkin Brave or OpenTelemetry). We then have to add a dependency for the vendor-specific integration into Micrometer Tracing:
````xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-brave</artifactId>
</dependency>
````

or

````xml
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-tracing-bridge-otel</artifactId>
</dependency>
````
Spring Actuator has auto-configuration for both tracers, i.e., it registers the vendor-specific objects and the Micrometer Tracing ObservationHandler implementations, delegating these objects into the application context. So there’s no need for further configuration steps.


