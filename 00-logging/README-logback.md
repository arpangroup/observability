# Logback Configuration
Let’s write a simple logback-spring.xml:
````xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOGS" value="./logs" />

    <appender name="Console"
        class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <appender name="RollingFile"
        class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/spring-boot-logger.log</file>
        <encoder
            class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d %p %C{1} [%t] %m%n</Pattern>
        </encoder>

        <rollingPolicy
            class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- rollover daily and when the file reaches 10 MegaBytes -->
            <fileNamePattern>${LOGS}/archived/spring-boot-logger-%d{yyyy-MM-dd}.%i.log
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>
    
    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </root>

    <!-- LOG "com.baeldung*" at TRACE level -->
    <logger name="com.baeldung" level="trace" additivity="false">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </logger>

</configuration>

````


## Filter in Logback
Logback allows you to apply filters to loggers or appenders to fine-tune what gets logged. For instance, you might want to exclude `DEBUG` level logs from being written to a file while still logging `INFO` and higher levels.
````xml
<configuration>

    <!-- File Appender with a Level Filter -->
    <appender name="FILTERED_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/filtered-app.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/filtered-app.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>

        <!-- Exclude DEBUG logs from being written to this file -->
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>DEBUG</level>
            <onMatch>DENY</onMatch>
            <onMismatch>ACCEPT</onMismatch>
        </filter>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="FILTERED_FILE" />
    </root>
</configuration>
````


## Implementing Multiple Log Files
For complex applications, you might want separate log files for different components: We will create two new classes MyController, and MyServices. Additionally, we will also modify the main to call these methods. We can implement separate logging mechanisms for each of these classes. The below configuration ensures that separate log files are created and different log levels are set for both.
````java
// Controller classs
package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
public class MyController {

    private static final Logger logger = LoggerFactory.getLogger(MyController.class);

    public String controllerLog() {
        logger.debug("This is a DEBUG message from the controller.");
        logger.info("This is an INFO message from the controller.");
        return "Check the controller.log for logs.";
    }
}
// Service Class
package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MyService {

    private static final Logger logger = LoggerFactory.getLogger(MyService.class);

    public void serviceLog() {
        logger.info("This is an INFO message from the service.");
        logger.error("This is an ERROR message from the service.");
    }
}
// LoggingApplication (main)
package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class LoggingApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingApplication.class, args);
    }

    @Bean
    CommandLineRunner run(MyService myService, MyController myController) {
        return args -> {
            myService.serviceLog();
            myController.controllerLog();  // Log from the controller as well
        };
    }
}
````
````xml
<configuration>

    <!-- Define properties for log file names -->
    <property name="CONTROLLER_LOG" value="controller.log" />
    <property name="SERVICE_LOG" value="service.log" />

    <!-- Define the appender for controller logs -->
    <appender name="CONTROLLER_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${CONTROLLER_LOG}</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${CONTROLLER_LOG}.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <!-- Define the appender for service logs -->
    <appender name="SERVICE_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${SERVICE_LOG}</file>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${SERVICE_LOG}.%d{yyyy-MM-dd}.gz</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>7</maxHistory>
            <totalSizeCap>1GB</totalSizeCap>
        </rollingPolicy>
    </appender>

    <!-- Define the controller logger -->
    <logger name="com.myapp.controllers" level="DEBUG" additivity="false">
        <appender-ref ref="CONTROLLER_FILE" />
    </logger>

    <!-- Define the service logger -->
    <logger name="com.myapp.services" level="INFO" additivity="false">
        <appender-ref ref="SERVICE_FILE" />
    </logger>

    <!-- Define the root logger -->
    <root level="ERROR">
        <appender-ref ref="CONTROLLER_FILE" />
        <appender-ref ref="SERVICE_FILE" />
    </root>

</configuration>
````

## Setting Up Asynchronous Logging
Asynchronous logging improves performance by offloading log processing to a separate thread:
````xml
<appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="CONTROLLER_FILE" />
    <appender-ref ref="SERVICE_FILE" />
</appender>
````



##  Conditional Logging Based on Profiles

Spring Boot includes a number of [extensions](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.logback-extensions) to Logback that can help with advanced configuration. You can use these extensions in your `logback-spring.xml` configuration file.
````xml
<springProfile name="staging">
	<!-- configuration to be enabled when the "staging" profile is active -->
</springProfile>

<springProfile name="dev | staging">
	<!-- configuration to be enabled when the "dev" or "staging" profiles are active -->
</springProfile>

<springProfile name="!production">
	<!-- configuration to be enabled when the "production" profile is not active -->
</springProfile>
````

We can configure Logback to apply different logging configurations based on the active Spring profile using the `<springProfile>` tag. This allows you to define separate logging behaviors for development, testing, and production environments.
````xml
<configuration>

    <!-- Default profile: logs INFO level and higher to the console -->
    <springProfile name="default">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="INFO">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>

    <!-- Development profile: logs DEBUG level and higher to the console -->
    <springProfile name="dev">
        <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="DEBUG">
            <appender-ref ref="CONSOLE" />
        </root>
    </springProfile>

    <!-- Production profile: logs WARN level and higher to a file -->
    <springProfile name="prod">
        <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
            <file>logs/app.log</file>
            <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
                <fileNamePattern>logs/app.%d{yyyy-MM-dd}.log</fileNamePattern>
                <maxHistory>30</maxHistory>
            </rollingPolicy>
            <encoder>
                <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
            </encoder>
        </appender>

        <root level="WARN">
            <appender-ref ref="FILE" />
        </root>
    </springProfile>

</configuration>

````