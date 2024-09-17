#FROM openjdk:21-jdk-oracle
#ARG JARFILE=target/*.jar
#COPY ./target/04-metrics-collector-with-otel-agent-0.0.1-SNAPSHOT.war app.war
#ENTRYPOINT ["java", "-jar", "/app.war"]

#FROM openjdk:21-jdk-oracle
#ADD target/api-app.jar /app.jar
#ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
#ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar \
#    -Dotel.service.name=order-service \
#    -Dotel.traces.exporter=logging \
#    -Dotel.metrics.exporter=logging \
#    -Dotel.logs.exporter=logging \
#    -jar /app.jar

FROM openjdk:21-jdk-oracle
ADD target/api-app.jar /api-app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar -jar /api-app.jar