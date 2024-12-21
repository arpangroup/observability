
# Spring Boot Application Configuration Properties

This document outlines various configuration properties available for Spring Boot applications. These properties can be set in your `application.properties` or `application.yml` files to control various aspects of your application.

---


## Table of Contents

- [Spring Application Properties](#spring-application-properties)
- [Datasource Configuration](#spring-datasource)
- [Server Configuration](#spring-server)
- [JPA Configuration](#spring-jpa)
- [Spring MVC Configuration](#spring-mvc)
- [Spring Cloud Configuration](#spring-cloud)
- [Messaging Configuration](#spring-messaging)
- [Security Configuration](#spring-security)
- [Logging Configuration](#spring-logging)
- [Redis Configuration](#spring-redis)
- [Cache Configuration](#spring-cache)
- [Kafka Configuration](#spring-kafka)
- [Actuator Configuration](#spring-actuator)
- [WebFlux Configuration](#spring-webflux)
- [Profiles Configuration](#spring-profiles)
- [Mail Configuration](#spring-mail)
- [FreeMarker Configuration](#spring-freemarker)
- [Thymeleaf Configuration](#spring-thymeleaf)
- [Liquibase Configuration](#spring-liquibase)



## Spring Boot Configuration Examples

### Application Properties
Defines the name of your Spring Boot application.

```yaml
spring:
  application:
    name: "ecs-app"
```

---

### Datasource Configuration
Used for configuring database connections.

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: rootpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
```

---

### Server Configuration
Configures the embedded server (e.g., Tomcat, Jetty, Undertow).

```yaml
spring:
  server:
    port: 8080
    servlet:
      context-path: /myapp
```

---

### JPA Configuration
Used to configure Java Persistence API (JPA) and Hibernate settings.

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5Dialect
```

---

### MVC Configuration
Configuration for Spring MVC (for web applications).

```yaml
spring:
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
```

---

### Cloud Configuration
For configuring Spring Cloud settings (e.g., service discovery, config server).

```yaml
spring:
  cloud:
    discovery:
      enabled: true
```

---

### Messaging Configuration

#### RabbitMQ
For configuring RabbitMQ settings.

```yaml
spring:
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

#### Kafka
For configuring Kafka messaging.

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-group
    producer:
      acks: all
```

---

### Security Configuration
For configuring Spring Security.

```yaml
spring:
  security:
    user:
      name: user
      password: password
    oauth2:
      login:
        client-id: my-client-id
        client-secret: my-client-secret
```

---

### Logging Configuration
For configuring application logging.

```yaml
spring:
  logging:
    level:
      root: INFO
      org.springframework.web: DEBUG
    file:
      name: logs/app.log
```

---

### Redis Configuration
For configuring Redis server settings.

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: mypassword
```

---

### Cache Configuration
For configuring caching.

```yaml
spring:
  cache:
    type: redis
```

---

### Actuator Configuration
For configuring Spring Boot Actuator endpoints.

```yaml
spring:
  actuator:
    endpoints:
      web:
        exposure:
          include: health,info
```

---

### WebFlux Configuration
For configuring WebFlux (reactive web applications).

```yaml
spring:
  webflux:
    base-path: /reactive
```

---

### Profiles Configuration
To set active profiles for different environments.

```yaml
spring:
  profiles:
    active: dev
```

---

### Mail Configuration
For configuring email sending.

```yaml
spring:
  mail:
    host: smtp.example.com
    port: 587
    username: user@example.com
    password: secret
```

---

### FreeMarker Configuration
For configuring FreeMarker template settings.

```yaml
spring:
  freemarker:
    template-loader-path: classpath:/templates/
```

---

### Thymeleaf Configuration
For configuring Thymeleaf template settings.

```yaml
spring:
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    cache: true
```

---

### Liquibase Configuration
For database migrations with Liquibase.

```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
```

---

## Additional Resources

For more advanced configurations and customizations, please refer to the official [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html).

---

This document provides a comprehensive overview of commonly used Spring Boot configuration properties across various domains such as database, server, messaging, security, logging, and more.



All properties:
````yml
spring:
  application:
    name: "hello-world-app"
  cache:
    type: redis
  cloud:
    discovery:
      enabled: true
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: rootpassword
    driver-class-name: com.mysql.cj.jdbc.Driver
  freemarker:
    template-loader-path: classpath:/templates/
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    database-platform: org.hibernate.dialect.MySQL5Dialect
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: my-group
    producer:
      acks: all
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
  logging:
    level:
      root: INFO
      org.springframework.web: DEBUG
    file:
      name: logs/app.log
  mail:
    host: smtp.example.com
    port: 587
    username: user@example.com
    password: secret
  mvc:
    view:
      prefix: /WEB-INF/views/
      suffix: .jsp
  profiles:
    active: dev
  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
  redis:
    host: localhost
    port: 6379
    password: mypassword
  security:
    user:
      name: "admin"
      password: "password"
  server:
    port: 8080
    oauth2:
      login:
        client-id: my-client-id
        client-secret: my-client-secret
  thymeleaf:
    prefix: classpath:/templates/
    suffix: .html
    cache: true
  web:
    servlet:
      context-path: /myapp
  webflux:
    base-path: /reactive

````
