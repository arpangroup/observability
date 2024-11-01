# Spring Boot Actuator
The actuator mainly exposes operational information about the running application — health, metrics, info, dump, env, etc
It uses HTTP endpoints or JMX beans to enable us to interact with it.
````
org.springframework.boot:spring-boot-starter-actuator
````

## 1.1. [Predefined Endpoints](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html)

Let’s have a look at some available endpoints, most of which were available in 1.x already.


| [Endpoints](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/production-ready-endpoints.html)   | [Description](https://docs.spring.io/spring-boot/docs/2.1.13.RELEASE/reference/html/production-ready-endpoints.html)                                                                                                 |
|-----------------|-----------------------------------------------------------------------------------------------------------------|
| /auditevents    | security audit-related events such as user login/logout.                                                        |
| /beans          | returns all available beans in our BeanFactory.                                                                 |
| /conditions     | formerly known as /autoconfig                                                                                   |
| /configprops    | allows us to fetch all @ConfigurationProperties beans.                                                          |
| /env            | environment properties                                                                                          |
| **/health**     | summarizes the health status of our application.                                                                |
| /heapdump       | heap dump from the JVM used by our application                                                                  |
| /info           | general information. It might be **custom data**, **build information** or details about the **latest commit**. |
| /logfile        | returns ordinary application logs.                                                                              |
| /loggers        | enables us to query and modify the logging level of our application                                             |
| **/mappings**   |                                                                                                                 |
| **/metrics**    | generic metrics as well as custom ones.                                                                         |
| /prometheus     | formatted to work with a Prometheus serve                                                                       |
| /scheduledtasks | [details about every scheduled task within our application                                                      |
| /sessions       | HTTP sessions, given we are using Spring Session                                                                |
| /shutdown       | performs a graceful shutdown of the application.                                                                |
| /threaddump     | the thread information of the underlying JVM.                                                                   |


## 1.2. Customize the actuator endpoint
1. `management`: This is the top-level configuration section for Spring Actuator.
2. `endpoints`: This subsection configures the behavior of Actuator endpoints.
   1. `web`: Configures the Actuator endpoints exposed over HTTP.
   2. `exposure`: Specifies which Actuator endpoints should be exposed and accessible via HTTP.
3. `endpoint`: This subsection configures specific Actuator endpoints.

````yml
management:
  endpoints:
    web:
      base-path: / # actuator endpoints are now pointed to /. i.e, /metrics instead of /actuator/metrics
      exposure:
        include: *
        exclude:    
  endpoint:
    health:
      show-details: always # [always, when-authorize, never]
    shutdown:
      enabled: false
````

## 1.3. [Customizing the Management Endpoint Paths](https://docs.spring.io/spring-boot/reference/actuator/monitoring.html#actuator.monitoring.customizing-management-server-context-path)
````properties
# changes the endpoint from /actuator/{id} to /manage/{id}  
# Ex: /manage/info, /manage/metrics, /manage/health 
management.endpoints.web.base-path=/manage 
````

## 1.4.1. [Info Contributor for static info](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.info)
````yml
info:
   myApp:
      encoding: 1.0
      java:
         source: 1.8
         target: 1.8
   techStack:
      database: oracle
      java: open jdk 8
      rontend: react
      spring-boot: 3.3
````

## 1.4.2. [Info Contributor for dynamic info](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.info)
````java
@Component
public class MyInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("example", Collections.singletonMap("key", "value"));
    }

}
````

## 1.5. [Custom Health Indicators](https://docs.spring.io/spring-boot/reference/actuator/endpoints.html#actuator.endpoints.health.writing-custom-health-indicators)
````java
@Component
public class MyHealthIndicator  implements HealthIndicator {

    @Override
    public Health health() {
        int errorCode = check();
        if (errorCode != 0) {
            return Health.down().withDetail("Error Code", errorCode).build();
        }
        return Health.up().build();
    }

    private int check() {
        // perform some specific health check
        return ...
    }
}
````

## 1.6. [Customizing the Management Server Port](https://docs.spring.io/spring-boot/reference/actuator/monitoring.html#actuator.monitoring.customizing-management-server-port)
Exposing management endpoints by using the default HTTP port is a sensible choice for cloud-based deployments. If, however, your application runs inside your own data center, you may prefer to expose endpoints by using a different HTTP port.
````properties
management.server.port=8081
````

## 1.7. [Configuring Management-specific SSL](https://docs.spring.io/spring-boot/reference/actuator/monitoring.html#actuator.monitoring.management-specific-ssl)
````properties
# Here both the main server and the management server can use SSL but with different key stores, as follows:
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:main.jks
server.ssl.key-password=secret

management.server.port=8080
management.server.ssl.enabled=true
management.server.ssl.key-store=classpath:management.jks
management.server.ssl.key-password=secret
````

## 1.8. Custom Actuator Endpoint using @Endpoint
curl -X GET http://localhost:8080/actuator/release-notes
````java

@Component
@Endpoint(id = "release-notes") // same like @Controller ==> /actuator/release-notes
public class CustomActuatorEndpoint {
   private Map<String, List<String>> releaseNotes = new LinkedHashMap<>();

   // GET  /actuator/release-notes
   @ReadOperation
   public Map<String, List<String>> getReleaseNotes() {
      return this.releaseNotes;
   }

   // GET /actuator/release-notes/{version}
   @ReadOperation
   public List<String> getReleaseNotesByVersion(@Selector String version) {
      return this.releaseNotes.get(version);
   }

   // POST  /actuator/release-notes
   @WriteOperation
   public void addReleaseNotes(@Selector String version, @Selector String releaseNotes) {
      List<String> notes = Arrays.stream(releaseNotes.split(", ")).toList();
      this.releaseNotes.put(version, notes);
   }

   // DELETE  /actuator/release-notes
   @DeleteOperation
   public void deleteNotes(@Selector String version) {
      this.releaseNotes.remove(version);
   }

   @PostConstruct
   public void init() {
      this.releaseNotes.put("v1.0", List.of("Home Page created", "Logo added to the navbar"));
      this.releaseNotes.put("v1.1", List.of("Login Page created", "session implemented", "cookies implemented"));
      this.releaseNotes.put("v1.2.", List.of("SSO implemented"));
   }
}

````


````java
@Endpoint(id = "sessions")
public class SessionsEndpoint {
   @ReadOperation
   public SessionsDescriptor getSession(@Selector String sessionId) {
      Session session = this.sessionRepository.findById(sessionId);
      if (session == null) return null;
      return new SessionsDescriptor(session);
   }
}
````


````java
@Endpoint(id = "loggers")
public class LoggersEndpoint {
   @WriteOperation
   public void configureLogLevel(@Selector String name, @Nullable String configuredLevel) {
      Assert.notNull(name, "Name must not be empty");
      this.loggingSystem.setLogLevel(name, configuredLevel);
   }
}
````


````java
@DeleteOperation
public void deleteSession(@Selector String sessionId) {
   this.sessionRepository.deleteById(sessionId);
}
````