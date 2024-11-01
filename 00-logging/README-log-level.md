## [Log Levels](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.log-levels) (FATAL < ERROR < WARN < INFO < DEBUG < TRACE)

All the supported logging systems can have the logger levels set in the Spring `Environment` (for example, in `application.properties`) by using `logging.level.<logger-name>=<level>` where `level` is one of TRACE, DEBUG, INFO, WARN, ERROR, FATAL, or OFF. The `root` logger can be configured by using `logging.level.root`.
````properties
-Dlogging.level.root=warn
-Dlogging.level.org.springframework.web=debug
logging.level.org.hibernate=error
````

## Example1:
Here in below example we have set the log level as `INFO` in `application.properties` file, and we print the log as `debug`. As priority of `INFO` is lesser than `DEBUG`, so the message `debug log` will not be print on log. 

````properties
# log level below INFO will not be print (eg: DEBUG, TRACE log will not print)
# priority: FATAL < ERROR < WARN < INFO < DEBUG < TRACE
logging.level.com.observability=INFO
````

````java
package com.observability;
        
public String sayHello() {
    log.error("error log");
    log.warn("warn log");
    log.info("info log");
    log.debug("debug log"); // no log output;
    log.trace("trace log"); // no log output;
    return "Hello World!";
}
````
````log
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.3.4)

2024-11-01T02:22:25.489+05:30  INFO 9972 --- [00-logging] [           main] c.observability.__logging.Application    : Starting Application using Java 21.0.2 with PID 9972 (D:\java-projects\observability\00-logging\target\classes started by arpan in D:\java-projects\observability)
2024-11-01T02:22:25.497+05:30  INFO 9972 --- [00-logging] [           main] c.observability.__logging.Application    : No active profile set, falling back to 1 default profile: "default"
2024-11-01T02:22:26.569+05:30  INFO 9972 --- [00-logging] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
2024-11-01T02:22:27.084+05:30  INFO 9972 --- [00-logging] [           main] c.observability.__logging.Application    : Started Application in 2.002 seconds (process running for 2.354)
2024-11-01T02:23:46.404+05:30 ERROR 9972 --- [00-logging] [nio-8080-exec-1] c.o.__logging.logback.LogbackController  : error log
2024-11-01T02:23:46.404+05:30  WARN 9972 --- [00-logging] [nio-8080-exec-1] c.o.__logging.logback.LogbackController  : warn log
2024-11-01T02:23:46.404+05:30  INFO 9972 --- [00-logging] [nio-8080-exec-1] c.o.__logging.logback.LogbackController  : info log
````