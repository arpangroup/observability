FROM openjdk:21-jdk-oracle
ADD target/spring-app.jar /app.jar
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
ENTRYPOINT java -javaagent:/opentelemetry-javaagent.jar -jar /app.jar
