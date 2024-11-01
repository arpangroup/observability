# Use a base image with JDK 21 (or the version you need)
FROM openjdk:21-jdk-oracle

# Set the working directory inside the container
WORKDIR /app

# Copy the jar file into the container
COPY target/spring-app.jar /app/app.jar

# Expose the application's port (optional)
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]