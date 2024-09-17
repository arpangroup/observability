https://opentelemetry.io/docs/languages/java/configuration/

````bash
docker system prune -a --volumes

````
````bash
docker compose up --force-recreate --remove-orphans --detach
````

# SpringBoot + Otel Java Agent + Otel Collector 
<img src="https://github.com/nlinhvu/opentelemetry-order-service-2023/blob/main/gif/diagram.gif?raw=true"/>

## 1. Let's start with some `docker-compose` bare minimal [configuration](https://opentelemetry.io/docs/languages/java/configuration/) without the `otel collector`

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
      - '9090:9090'                            # Prometheus UI
    depends_on:
      - order-service

  zipkin-all-in-one:
    container_name: zipkin
    image: openzipkin/zipkin:latest
    restart: always
    ports:
      - "9411:9411"

  grafana:
    container_name: grafana
    image: grafana/grafana
    volumes:
      - ./docker/grafana/grafana-datasources.yml:/etc/grafana/provisioning/datasources/datasources.yml
    restart: always
    ports:
      - "3000:3000"
    depends_on:
      - prometheus
````

- **TRACES:**   `Otel Java Agent` push the `traces` to the `zipkin` endpoint(http://localhost:9411/). 
- **METRICS:**  `Otel Java Agent` expose an endpoint(`0.0.0.0:9464`) where `prometheus` will query / scrap for metrics information. Prometheus UI endpoint: http://localhost:9090/.
- **[LOGS:](https://opentelemetry.io/docs/languages/java/instrumentation/#logs)** Logs are distinct from metrics and traces in that <b>there is no user-facing OpenTelemetry logs API.</b>
  Instead, there is tooling to bridge logs from existing popular log frameworks (e.g. SLF4j, JUL, Logback, Log4j) into the OpenTelemetry ecosystem. For rationale behind this design decision, see [Logging specification.](https://opentelemetry.io/docs/specs/otel/logs/)

## 2. What about [Log](https://opentelemetry.io/docs/languages/java/instrumentation/#logs) in OpenTelementry Agent?
[Logs]((https://opentelemetry.io/docs/languages/java/instrumentation/#logs)) are distinct from metrics and traces in that <b>there is no user-facing OpenTelemetry logs API.</b>
Instead, there is tooling to bridge logs from existing popular log frameworks (e.g. SLF4j, JUL, Logback, Log4j) into the OpenTelemetry ecosystem. For rationale behind this design decision, see [Logging specification.](https://opentelemetry.io/docs/specs/otel/logs/)

The reason OpenTelemetry agents historically did not provide logging is that OpenTelemetry's original focus was on distributed tracing and metrics. **Logging was considered a separate concern, often handled by other logging frameworks.** The OpenTelemetry project aimed to standardize tracing and metrics first to unify observability across distributed systems.

**Solution:** **OpenTelemetry Collector**

## 3. Otel Collector to collect all Logs, Metrics & Traces
<img src="https://b3716232.smushcdn.com/3716232/wp-content/uploads/2022/07/OpenTelemetry-Collector-Architecture.png?lossy=2&strip=1&webp=1"/>
The OpenTelemetry Collector is essential because it centralizes, processes, and exports telemetry data (like metrics, logs, and traces) from various sources. It provides flexibility in data collection, allowing you to handle different formats, transform data, and forward it to backends like Prometheus, Jaeger, or others, improving observability and reducing the burden on individual services.

The Collector is made up of the following components:
- **Receivers:** The receiver enters data into the collector. There are two types of receivers: `push-based` and `pull-based.` A receiver typically receives data in a specified format, converts it to an internal format, and passes it to processors and exporters defined in the OpenTelemetry pipeline. Traces and metrics can have a receiver-specific format.
- **Processors:** Processors are used to process data before sending it to export. They can transform metrics and rename spans. They also support batching data before sending, retrying if an export fails, adding metadata, and performing tail-based sampling.
- **Exporters:** Exporters can export data to several open source and commercial back-ends. For example, a console exporter makes it possible to export log data to console output, while a file export can dump data to a file.
- **The OpenTelemetry Pipeline:** The preceding three components—receivers, processors, and exporters—make up the Open Telemetry Pipeline, which define how telemetry data is collected and handled. <br/><br/>In addition to pipeline components, there are two other components that assist in data handling and communication.
- **Extensions:** Extensions are optional, and provide additional functionality not supported by the default collector. They do not need direct access to telemetry data. Three extensions provided by OpenTelemetry are health_check, pprof, and zpages (learn more in the [documentation](https://opentelemetry.io/docs/collector/configuration/#extensions)). You can also create your own extensions.
- **Service:** Services are used to enable components within receivers, processors, exports, and extensions. The service section of the configuration consists of two subsections: extensions and pipes.
  - The extensions section contains a list of all extensions you want to activate.
  - The pipeline section can define traces, metrics, or logs. Each pipeline consists of a set of receivers, processors, and exports. Each of these must first be defined in configuration sections outside the services section, and can then be referenced in this section to be included in a pipeline.



## 1.1. Logs using `otel-collector` & `Locki`
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
      OTEL_LOGS_EXPORTER: "otlp"
      OTEL_EXPORTER_OTLP_LOGS_ENDPOINT: "http://collector:4317"
      #      OTEL_EXPORTER_OTLP_LOGS_ENDPOINT: "http://collector:4318"
      OTEL_EXPORTER_OTLP_HEADERS: "client_id=abc"
      OTEL_EXPORTER_OTLP_PROTOCOL: "grpc"  # ["grpc", "http/protobuf", "http/json"]
    ports:
      - "8080:8080"
    depends_on:
      - zipkin-all-in-one
      - collector

  collector:
    container_name: collector
    #    image: otel/opentelemetry-collector-contrib
    image: otel/opentelemetry-collector-contrib:0.76.1
    command:
      - --config=/etc/otelcol-contrib/otel-collector.yml
    volumes:
      - ./docker/collector/otel-collector.yml:/etc/otelcol-contrib/otel-collector.yml
    restart: always
    ports:
      - "4317:4317" # OTLP gRPC receiver
      - "4318:4318" # OTLP http receiver
    depends_on:
      - loki

  loki:
    container_name: loki
    image: grafana/loki:3.1.0
    command: -config.file=/etc/loki/local-config.yaml
    restart: always
    ports:
      - "3100:3100"

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

  grafana:
    container_name: grafana
    image: grafana/grafana
    environment:
      - "GF_AUTH_DISABLE_LOGIN_FORM=true"
      - "GF_AUTH_ANONYMOUS_ENABLED=true"
      - "GF_AUTH_ANONYMOUS_ORG_ROLE=Admin"
    volumes:
      - ./docker/grafana/grafana-datasources.yml:/etc/grafana/provisioning/datasources/datasources.yml
    restart: always
    ports:
      - "3001:3000"
    depends_on:
      - prometheus
````

otel-collector.yml
````yml
extensions:
  basicauth/loki:
    client_auth:
      username: admin
      password: admin

receivers:
  otlp:
    protocols:
      grpc:
      http:

exporters:
  otlphttp/loki:
    endpoint: "http://loki:3100/otlp"
    tls:
      insecure: true

service:
  pipelines:
    logs:
      receivers: [otlp]
      exporters: [otlphttp/loki]
````