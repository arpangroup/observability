https://opentelemetry.io/docs/languages/java/instrumentation/

## 1. Standard SpringBoot-2 + [OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar)
<img src="01_spring2_with_otel_java_agent.png"/>

 1. Attach OpenTelemetry Agent Only for standard metrics
 2. Add OpenTelemetry API for custom metrics
 3. <b>Micrometer to create custom metrics instead of opentelemetry-api, as Micrometer is the default in spring & we dont need external opentelemetry-api for custom metrics </b>


#### <br/>[OpenTelemetry Java Instrumentation Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar)
> 1. Download from [github.com/open-telemetry/opentelemetry-java-instrumentation](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar)
> 2. mvn clean install <== Build the JAR/WAR file
> 3. java `-javaagent:`C:\opentelemetry-javaagent.jar `-jar` target/demo-service-0.0.1-SNAPSHOT.war

````bash
java -javaagent:C:\opentelemetry-javaagent.jar -jar target/04-metrics-collector-with-otel-agent-0.0.1-SNAPSHOT.war
````

- Just use OpenTelemetry (otel) Java agent and see the magic without any extra SpringBoot configuration
- Metrics will be generated automatically without any extra spring dependency
- we can view the metrics in grafana
- Create an endpoint on SpringBoot "/greet" and Hit multiple times
- EX:
    - http_server_duration_milliseconds_count{job="metrics-spring-2-otel"}


## [Configuring the agent](https://opentelemetry.io/docs/zero-code/java/agent/getting-started/)
One option is to pass configuration properties via the `-D` flag. In this example, a service name and Zipkin exporter for traces are configured:
````
java -javaagent:H:\opentelemetry-javaagent.jar \
     -Dotel.service.name=demo-service \
     -Dotel.traces.exporter=zipkin \
     -jar target/demo-app-0.0.1-SNAPSHOT.war

````
You can also use environment variables to configure the agent:
````
OTEL_SERVICE_NAME=your-service-name \
OTEL_TRACES_EXPORTER=zipkin \
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -jar myapp.jar
````


You can also supply a Java properties file and load configuration values from there:
````
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -Dotel.javaagent.configuration-file=path/to/properties/file.properties \
     -jar myapp.jar
````
or
````
OTEL_JAVAAGENT_CONFIGURATION_FILE=path/to/properties/file.properties \
java -javaagent:path/to/opentelemetry-javaagent.jar \
     -jar myapp.jar
````


## 2. Setup OpenTelemetry using Docker - Bare Minimal
````dockerfile
FROM openjdk:21-jdk-oracle
ARG JARFILE=target/*.jar
COPY ./target/order-service-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.war"]
````

or

````dockerfile
FROM openjdk:21-jdk-oracle
ADD target/order-service-SNAPSHOT.jar /app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar \
    -Dotel.service.name=order-service \
    -Dotel.traces.exporter=logging \
    -Dotel.metrics.exporter=logging \
    -Dotel.logs.exporter=logging \
    -jar /app.jar
````

or

````dockerfile
FROM openjdk:21-jdk-oracle
ADD target/order-service-SNAPSHOT.jar /api-app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar -jar /api-app.jar
````



## 3. Setup SpringBoot with OpenTelemetry JavaAgent using `docker-compose`
Bare Minimal: Setup `order-service` with `OpenTelemetry JavaAgent` using `docker-compose`. 
Here the build will refer to the Dockerfile which will run and create a image from the springboot jar file mentioned on /target directory.
````yml
services:
  order-service:
      build: ./                             # refer to the above Dockerfile & create a docker image
      environment:
        OTEL_SERVICE_NAME: "order-service"
        OTEL_TRACES_EXPORTER: "logging"     # use an exporter that writes all traces to the console
        OTEL_METRICS_EXPORTER: "logging"    # use an exporter that writes all metrics to the console
        OTEL_LOGS_EXPORTER: "logging"       # use an exporter that writes all logs to the console
      ports:
        - "8080:8080"
````

<br/><b>TRACES: </b> Configure `Otel Java Agent` to export `traces` to Zipkin
````yml                        
environment:
  OTEL_TRACES_EXPORTER: "zipkin"
  OTEL_EXPORTER_ZIPKIN_ENDPOINT: "http://zipkin:9411/api/v2/spans"
      
zipkin-all-in-one:
  container_name: zipkin
  image: openzipkin/zipkin:latest
  restart: always
  ports:
    - "9411:9411"
````

<br/><b>METRICS: </b>Configure `Otel Java Agent` to expose `metrics` endpoint for `Prometheus`
prometheus will query / pull / scrap the metrics to the OtelJavaAgent provided endpoint(`0.0.0.0:9464`) 
````yml                     
  environment:
    OTEL_METRICS_EXPORTER: "prometheus"
    OTEL_EXPORTER_PROMETHEUS_HOST: "0.0.0.0" # prometheus exporter: prometheus will query to this endpoint
    OTEL_EXPORTER_PROMETHEUS_PORT: "9464"    # prometheus exporter: prometheus will query to this port  

  prometheus:
    container_name: prometheus
    image: prom/prometheus
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - --config.file=/etc/prometheus/prometheus.yml
    restart: always
    ports:
      - '9090:9090'
    depends_on:
      - order-service
````
## [LOGS](https://opentelemetry.io/docs/languages/java/instrumentation/#logs)
Logs are distinct from metrics and traces in that <b>there is no user-facing OpenTelemetry logs API.</b>
Instead, there is tooling to bridge logs from existing popular log frameworks (e.g. SLF4j, JUL, Logback, Log4j) into the OpenTelemetry ecosystem. For rationale behind this design decision, see [Logging specification.](https://opentelemetry.io/docs/specs/otel/logs/)

<br/>
The two typical workflows discussed below each cater to different application requirements.

### 1. Direct to collector
In the direct to collector workflow, logs are emitted directly from an application to a collector using a network protocol (e.g. OTLP). This workflow is simple to set up as it doesn’t require any additional log forwarding components, and allows an application to easily emit structured logs that conform to the [log data model](https://opentelemetry.io/docs/specs/otel/logs/data-model). However, the overhead required for applications to queue and export logs to a network location may not be suitable for all applications.

To use this workflow:
 - Install appropriate [Log Appender.](https://opentelemetry.io/docs/languages/java/instrumentation/#log-appenders)
 - Configure the OpenTelemetry [Log SDK](Configure the OpenTelemetry Log SDK to export log records to desired target destination (the collector or other)) to export log records to desired target destination (the [collector](https://github.com/open-telemetry/opentelemetry-collector) or other)

### 2. Log appenders
A log appender bridges logs from a log framework into the OpenTelemetry [Log SDK](https://opentelemetry.io/docs/languages/java/instrumentation/#logs-sdk) using the [Logs Bridge API.](https://opentelemetry.io/docs/specs/otel/logs/bridge-api) Log appenders are available for various popular Java log frameworks:
 - [Log4j2 Appender](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/log4j/log4j-appender-2.17/library)
 - [Logback Appender](https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/logback/logback-appender-1.0/library)


### All together the `docker-compose` file
````yml
services:
  order-service:
    build: ./                                  # refer to the above Dockerfile & create a docker image
    environment:
      OTEL_SERVICE_NAME: "order-service"
      OTEL_TRACES_EXPORTER: "zipkin"
      OTEL_EXPORTER_ZIPKIN_ENDPOINT: "http://zipkin:9411/api/v2/spans"
      OTEL_METRICS_EXPORTER: "prometheus"
      OTEL_EXPORTER_PROMETHEUS_HOST: "0.0.0.0" # prometheus exporter: prometheus will query to this endpoint
      OTEL_EXPORTER_PROMETHEUS_PORT: "9464"    # prometheus exporter: prometheus will query to this port
      OTEL_LOGS_EXPORTER: "logging"
    ports:
      - "8080:8080"
    depends_on:
      - zipkin-all-in-one

  prometheus:
    container_name: prometheus
    image: prom/prometheus
    volumes:
      - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml
    command:
      - --config.file=/etc/prometheus/prometheus.yml
    restart: always
    ports:
      - '9090:9090'
    depends_on:
      - order-service

  zipkin-all-in-one:
    container_name: zipkin
    image: openzipkin/zipkin:latest
    restart: always
    ports:
      - "9411:9411"
````

<br/><hr/>

## 2. Custom Metrics with [OpenTelemetryAPI](https://mvnrepository.com/artifact/io.opentelemetry/opentelemetry-api)
- ### Why we need custom metrics if otel does everything?
    - Its always good practice to expose information about whats going on our business logic

````
<dependency>
    <groupId>oio.opentelemetry</groupId>
    <artifactId>opentelemetry-api</artifactId>
</dependency>
````

````java
counter = GlobalOpenTelemetry.get()
    .meterBuilder("my-custom-instrumentation")
    .setInstrumentationVersion("1.0.0")
    .build()
    .counterBuilder("my.custom.counter")
    .setDescription("Custom Counter")
    .setUnit("1")
    .build();


counter.increment(); // inside /greeting api:
````
> Use Micrometer to create custom metrics instead of `opentelemetry-api`, 
> as <b>Micrometer is the default metrics library in spring</b> & we don't need an external `opentelemetry-api` just for custom metrics

You need actuator dependency for custom metrics using Micrometer
````xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
````


<br/><hr/>
## 3. SpringBoot-2 + Spring's Default Metrics Library (Micrometer) + [OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v2.7.0/opentelemetry-javaagent.jar)
<img src="02_spring2_with_micrometer_and_otel_java_agent.png"/>

### What should we do with OpenTelemetry if we already have the default Spring Metrics?
- OpenTelemetry Java Agent actually recognizes the SpringBoot actuator
- All actuator metrics will be picked-up by the OpenTelemetry agent along with its own metrics
- i.e., we can see both otel metrics & actuator metrics
- i.e., there might be some <red>**duplicate**</red> metrics
    - springboot actuator metric: http.server.request=true ====> http_server_request_milliseconds_count{job="metrics-spring-2-micrometer-custom"}
    - OpenTelemetry(otel) metric: http_server_duration_milliseconds_count{job="metrics-spring-2-otel"}
        - here otel & springboot has different names, but the semantic/content is very similar.
        - <span style="color:yellow">otel tell them as "http_server_duration" but micrometer tells them as "http_server_request"</span>

### Avoid Duplicate Metric Data: Disable Micrometer metrics in otel agent:
````
- Configure the OpenTelemetry to DISABLE Micrometer metrics
  - -Dotel.instrumentation.micrometer.enabled=false
- Configure the OpenTelemetry to use ONLY Micrometer metrics
  - -Dotel.instrumentation.common.default-enabled=false
  - -Dotel.instrumentation.micrometer.enabled=true
  - -Dotel.instrumentation.spring-boot-actuator-autoconfigure.enabled=true
````
