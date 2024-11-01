
## [Logging in SpringBoot](https://docs.spring.io/spring-boot/reference/features/logging.html)
<img src="logging.png"/>

Spring Boot uses [Commons Logging](https://commons.apache.org/logging) for all internal logging but leaves the underlying log implementation open.

Default configurations are provided for [Java Util Logging](https://docs.oracle.com/en/java/javase/17/docs/api/java.logging/java/util/logging/package-summary.html), [Log4j2](https://logging.apache.org/log4j/2.x/), and [Logback](https://logback.qos.ch/)

By default, if you use the starters, Logback is used for logging

## [Log Format](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.log-format)
The default log output from Spring Boot resembles the following example:
````
2024-09-19T09:38:56.347Z  INFO 111370 --- [myapp] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8080 (http)
````
- Date and Time: Millisecond precision and easily sortable.
- Log Level: ERROR, WARN, INFO, DEBUG, or TRACE.
- Process ID.
- A --- separator to distinguish the start of actual log messages.
- Application name: Enclosed in square brackets (logged by default only if spring.application.name is set)
- Thread name: Enclosed in square brackets (may be truncated for console output).
- Correlation ID: If tracing is enabled (not shown in the sample above)
- Logger name: This is usually the source class name (often abbreviated).
- The log message.

> Note: If you have a spring.application.name property but don’t want it logged you can set logging.include-application-name to false.

## [Console Output](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.console-output)
By default, `ERROR`-level, `WARN`-level, and `INFO`-level messages are logged. You can also enable a “debug” mode by starting your application with a --debug flag.
````
$ java -jar myapp.jar --debug
````

## [File Output](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.file-output)
````properties
# If both properties are set, logging.file.path is ignored and only logging.file.name is used.
logging.file.name=myapp.log
logging.file.path=C://log
````

## [File Rotation](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.file-rotation)
If you are using the `Logback`, it is possible to fine-tune log rotation settings using your `application.properties` file.

For all other logging system, you will need to configure rotation settings directly yourself (for example, if you use Log4j2 then you could add a `log4j2.xml` or `log4j2-spring.xml` file).

|  Name | Description                                                            
|-------|------------------------------------------------------------------------|
| logging.logback.rollingpolicy.file-name-pattern    | The filename pattern used to create log archives.                                                                       |
| logging.logback.rollingpolicy.clean-history-on-start    | If log archive cleanup should occur when the application starts.                                                                       |
| logging.logback.rollingpolicy.max-file-size    | The maximum size of log file before it is archived.                    |
| logging.logback.rollingpolicy.total-size-cap    | The maximum amount of size log archives can take before being deleted. |
| logging.logback.rollingpolicy.max-history    | The maximum number of archive log files to keep (defaults to 7).       |

````properties
logging.logback.rollingpolicy.file-name-pattern=myapp.%d{yyyy-MM-dd}.%i.gz.log
logging.logback.rollingpolicy.max-file-size=10MB  # Daily log rotation (adjust if needed)
logging.logback.rollingpolicy.total-size-cap=1GB  # Total log size limit
logging.logback.rollingpolicy.max-history=7       # Keep logs for 7 days
logging.logback.rollingpolicy.clean-history-on-start=true  # Clean up old logs on startup
````

## Customizing Log Format and Patterns
````properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread/%X{userId}] %-7level %logger{20} - %msg [%file:%line]%n
````

## Handling Log File Permissions and Access Control
Ensure that your log files are secure by setting appropriate permissions. For example:
````properties
# Set the owner to the application user and group
chown appuser:appgroup /var/log/app.log

# Set permissions to allow the owner to read/write, group to read, and no access for others
chmod 640 /var/log/app.log
````

or using logback-spring.xml
````xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>/var/log/app.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
        <fileNamePattern>/var/log/app.%d{yyyy-MM-dd}.log</fileNamePattern>
        <maxHistory>30</maxHistory>
    </rollingPolicy>
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} - %msg%n</pattern>
    </encoder>
    <prudent>false</prudent>
    <fileNamePatternPermissions>rw-r-----</fileNamePatternPermissions>
</appender>

````


## [Log Levels](https://docs.spring.io/spring-boot/reference/features/logging.html#features.logging.log-levels) (FATAL < ERROR < WARN < INFO < DEBUG < TRACE)
All the supported logging systems can have the logger levels set in the Spring `Environment` (for example, in `application.properties`) by using `logging.level.<logger-name>=<level>` where `level` is one of TRACE, DEBUG, INFO, WARN, ERROR, FATAL, or OFF. The `root` logger can be configured by using `logging.level.root`.
````properties
-Dlogging.level.root=warn
-Dlogging.level.org.springframework.web=debug
logging.level.org.hibernate=error
````

or
````properties
mvn spring-boot:run 
  -Dspring-boot.run.arguments=--logging.level.org.springframework=TRACE,--logging.level.com.baeldung=TRACE
````

## log4j logging hierarchy order:

|       | Telemetry | Library | Collector | INFO | DEBUG | TRACE | ALL
|-------|-----------|---------|-----------|------|-------|-------|----
| OFF   |           |         |           |  |       |  |
| FATAL | X         |         |           |  |       |  |
| ERROR | X         | X       |           |  |       |  |
| WARN  | X         | X       | X         |  |       |  |
| INFO  | X         | X       | X         | X |       |  |
| DEBUG | X         | X       | X         | X | X     |  |
| TRACE | X         | X       | X         | X | X     | X |
| ALL   | X         | X       | X         | X | X     | X | X




## 7. Logging With Lombok
### 7.1. @Slf4j and @CommonsLog
````java
@RestController
@Slf4j
public class LombokLoggingController {
 
    @RequestMapping("/lombok")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");
 
        return "Howdy! Check out the Logs to see the output...";
    }
}
````
### 7.2. @Log4j2
````java
@RestController
@Log4j2
public class LombokLoggingController {

    @RequestMapping("/lombok")
    public String index() {
        log.trace("A TRACE Message");
        log.debug("A DEBUG Message");
        log.info("An INFO Message");
        log.warn("A WARN Message");
        log.error("An ERROR Message");

        return "Howdy! Check out the Logs to see the output...";
    }
}

````


## [Correlating Log and Trace using Digma](https://digma.ai/calling-a-spring-bean-from-custom-logback-appender-class/)

## Encoders
Encoders are responsible for transforming an incoming event into a byte array and writing out the resulting byte-array onto the appropriate `OutputStream.`

In our configuration file, a “CONSOLE_JSON” appender is set with an encoder of type `LoggingEventCompositeJsonEncoder`. When this encoder is used, it’s mandatory to define the providers we want to use.
````xml
<appender name="CONSOLE-JSON" class="ch.qos.logback.core.ConsoleAppender">
  <encoder class="net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder">
    <providers>
        <arguments>
            <fieldName>context</fieldName>
        </arguments>
        <pattern>
          <pattern>
              {
              "date": "%date{ISO8601,GMT+2}",
              "priority": "%level",
              "pid": "${PID:- }",
              "thread": "%t",
              "logger": "%logger",
              "message": "%.-10000msg",
              "stacktrace": "%.-10000throwable",
              "team": "%property{team}"
              }
          </pattern>
        </pattern>
        <mdc>net.logstash.logback.composite.loggingevent.MdcJsonProvider</mdc>
    </providers>
  </encoder>
</appender>
````
For this configuration, we use:
- Arguments provider → Outputs fields from the event arguments array that are defined by [Structured Arguments](https://github.com/logfellow/logstash-logback-encoder#event-specific-custom-fields). Adding the `<fieldName>context</fieldName>` element will add the field “context” with its value into the JSON output
- pattern provider → Outputs fields from a configured JSON Object string, substituting patterns supported by logback’s `PatternLayout`. Refer to official documentation about [PatternLayout](https://logback.qos.ch/manual/layouts.html#ClassicPatternLayout) and [Pattern JSON Provider](https://github.com/logfellow/logstash-logback-encoder#pattern-json-provider).
- mdc provider → defined by the class “MDCJsonProvider”. Outputs entries from the [Mapped Diagnostic Context (MDC).](https://logback.qos.ch/manual/mdc.html)



## [Mask, Hide & Replace Sensitive Data In Spring Boot Logs](https://youtu.be/3YK6UZq_51E?si=5ELBDqOIIUeWemWf)
<img src="replace_expression.jpg"/>
<img src="replace_expression_2.jpg"/>


## [Writing your own custom Layout](https://logback.qos.ch/manual/layouts.html#writingYourOwnLayout)
````java
public class MySampleLayout extends LayoutBase<ILoggingEvent> {

  public String doLayout(ILoggingEvent event) {
    StringBuffer sbuf = new StringBuffer(128);
    sbuf.append(event.getTimeStamp() - event.getLoggingContextVO.getBirthTime());
    sbuf.append(" ");
    sbuf.append(event.getLevel());
    sbuf.append(" [");
    sbuf.append(event.getThreadName());
    sbuf.append("] ");
    sbuf.append(event.getLoggerName();
    sbuf.append(" - ");
    sbuf.append(event.getFormattedMessage());
    sbuf.append(CoreConstants.LINE_SEP);
    return sbuf.toString();
  }
}
````
Note that `MySampleLayout` extends `LayoutBase`. This class manages state common to all layout instances, such as whether the layout is started or stopped, header, footer and content type data.

````xml
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.core.encoder.LayoutWrappingEncoder">
      <layout class="chapters.layouts.MySampleLayout" />
    </encoder>
  </appender>

  <root level="DEBUG">
    <appender-ref ref="STDOUT" />
  </root>
</configuration>
````


## Creating a Custom Logback Appender
````java
public class MapAppender extends AppenderBase<ILoggingEvent> {
    private ConcurrentMap<String, ILoggingEvent> eventMap = new ConcurrentHashMap<>();
    private String prefix;
    
    @Override
    protected void append(ILoggingEvent event) {
        if (prefix == null || "".equals(prefix)) {
            addError("Prefix is not set for MapAppender.");
            return;
        }
        eventMap.put(prefix + System.currentTimeMillis(), event);
    }

    public Map<String, ILoggingEvent> getEventMap() {
        return eventMap;
    }
    public String getPrefix() { return prefix;}

    public void setPrefix(String prefix) { this.prefix = prefix;}
}
````
Next, to enable the MapAppender to start receiving logging events, let’s add it as an appender in our configuration file logback.xml:
````xml
<configuration debug="true">
    <appender name="map" class="com.baeldung.logback.MapAppender">
        <prefix>test</prefix>
    </appender>
    <root level="info">
        <appender-ref ref="map"/>
    </root>
</configuration>
````


## [Log4J vs Log4J2 vs Logback](https://logback.qos.ch/performance.html#:~:text=By%20default%2C%20log4j%201.2%20uses,ring%20buffer%20should%20favor%20log4j2.)

<table>
  <tr>
    <td></td>
    <td>Log4J2</td>
    <td>Logback</td>
  </tr>
  <tr>
    <td>Buffer</td>
    <td>
        By default:
        <li>log4j 1.2 uses a <b>circular buffer size of 128</b> </li>
        <li>log4j2 uses a <b>ring buffer size of 262'144 </b></li>
    </td>
    <td>
        <li>logback is set to a buffer size of 256.</li>
        <b>In principle, the much larger ring buffer should favor log4j2.</b>
    </td>
  </tr>
  <tr>
    <td rowspan="2">Performance</td>
    <td>When comparing the performance of apache log4j synchronous logger to logback, then log4j is 25 % much faster.</td>
    <td>When the logback is compared to logback synchronous logger, then the logback was worse in performance but only with the logback versions less than 1.2.1. Therefore the newer versions are of logback is a successor to log4j.</td>
  </tr>
  <tr>
    <td>Log4j’s synchronous appender is the fastest among any other logging frameworks, but log4j’s asynchronous appender cannot resolve bugs in multithreading logging.</td>
    <td>Logback asynchronous appender is also not recommended for multithreading logging as this also cannot debug the bugs, and logback synchronous appenders performance is less when compared to log4j.</td>
  </tr>
  <tr>
    <td><a href="https://logback.qos.ch/performance.html#:~:text=By%20default%2C%20log4j%201.2%20uses,ring%20buffer%20should%20favor%20log4j2.">Benchmark</a></td>
    <td></td>
    <td>
        <li>logback version 1.3-alpha10 performs about 3 times faster than log4j and about 1.6 times faster than log4j2 in case of synchronous logging. </li>
        <li> For asynchronous logging, logback 1.3 performs 2.5 faster than log4j and 2.3 times faster than log4j2.</li>
    </td>
  </tr>
  <tr>
    <td>Configuration</td>
    <td>
        log4j.appender.file=org.apache.log4j.FileAppender 
        <br/>
        log4j.appender.file.File=app.log 
        <br/>
        log4j.appender.file.layout=org.apache.log4j.PatternLayout
        <br/>
        log4j.appender.file.layout.ConversionPattern=%d{HH:mm:ss,SSS} %-5p [%c] - %m%n
        <br/>
        <br/>
        log4j.rootLogger=info, file
        <br/>
        # basic log level for all messages
        <br/>
        log4j.logger.org.hibernate=info
        <br/>
        <br/>
        # SQL statements and parameters
        <br/>
        log4j.logger.org.hibernate.SQL=debug
        <br/>
        log4j.logger.org.hibernate.type.descriptor.sql=trace
    </td>
    <td>
<pre>
&lt;configuration&gt;
    &lt;appender name="FILE" class="ch.qos.logback.core.FileAppender"&gt;
        &lt;file&gt;app.log&lt;/file&gt
        &lt;encoder&gt;
            &lt;pattern&gt; %d{HH:mm:ss,SSS} %-5p [%c] - %m%n &lt;/pattern&gt;
        &lt;/encoder&gt
    &lt;/appender&gt
    &lt;logger name="org.hibernate.SQL" level="DEBUG" /&gt;
    &lt;logger name="org.hibernate.type.descriptor.sql" level="TRACE" /&gt;
    &lt;root level="info" &gt;
        &lt;appender-ref="FILE" /&gt;
    &lt;/root&gt;
&lt;/configuration&gt;

</pre>
    </td>
  </tr>
  <tr>
    <td>Conclusion</td>
    <td colspan="2">
        <li>Logback was written by the same developer who implemented Log4j with the goal to become its successor.  It follows the same concepts as Log4j but was rewritten to improve the performance, to support SLF4J natively,</li>
        <li>It concludes that there is no much difference in these logging frameworks based on Java applications. The log4j is less considered than logback only when there were no higher logback versions, but now logback is considered faster than log4j. Therefore we cannot recommend any java logging utility based on the performance as buffered handler implementations impact it. So there is no much difference that can be figured in log4j and logback.</li>
    </td>
  </tr>
 </table>

The above [benchmark](https://logback.qos.ch/performance.html#:~:text=By%20default%2C%20log4j%201.2%20uses,ring%20buffer%20should%20favor%20log4j2.) results show that throughput in synchronous logging is actually higher than that of asynchronous logging.



# FAQ:
1. What is default logging in SpringBoot?
    - When using starters, Logback is used for logging by default.[logback](https://logback.qos.ch/)
2. Logback vs Log4J
    - [Reasons to prefer logback over log4j 1.x](https://logback.qos.ch/reasonsToSwitch.html)
        - <b>Faster implementation:</b> Based on our previous work on log4j 1.x, logback internals have been re-written to perform about ten times faster on certain critical execution paths. Not only are logback components faster, they have a smaller memory footprint as well.
        - [Filters](https://logback.qos.ch/reasonsToSwitch.html#filters): Logback comes with a wide array of filtering capabilities going much further than what log4j 1.x has to offer. For example, let's assume that you have a business-critical application deployed on a production server. Given the large volume of transactions processed, logging level is set to WARN so that only warnings and errors are logged. Now imagine that you are confronted with a bug that can be reproduced on the production system but remains elusive on the test platform due to unspecified differences between those two environments (production/testing).
          <br/> <br/>With log4j 1.x, your only choice is to lower the logging level to DEBUG on the production system in an attempt to identify the problem. Unfortunately, this will generate large volume of logging data, making analysis difficult. More importantly, extensive logging can impact the performance of your application on the production system.
          <br/><br/>With logback, you have the option of keeping logging at the WARN level for all users except for the one user, say Alice, who is responsible for identifying the problem. When Alice is logged on, she will be logging at level DEBUG while other users can continue to log at the WARN level. This feat can be accomplished by adding 4 lines of XML to your configuration file. Search for MDCFilter in the relevant section of the manual.

# References:
- Latency and Throughput With Logback: https://tersesystems.com/blog/2022/10/16/latency-and-throughput-with-logback/
- Github Repository : https://github.com/FacuRamallo/Logging-Agregation-System-ELKK
- Further reading: https://medium.com/@facuramallo8/logging-aggregation-system-d94f60f92dd0
- LogBack Appenders: https://logback.qos.ch/manual/appenders.html
- https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/howto-logging.html
- https://github.com/logfellow/logstash-logback-encoder#encoders--layouts
- https://stegard.net/2021/02/spring-boot-http-access-logging-in-three-steps/
- https://github.com/spring-projects/spring-boot/blob/v3.2.0/spring-boot-project/spring-boot/src/main/resources/org/springframework/boot/logging/logback/defaults.xml
- https://sematext.com/glossary/structured-logging/#:~:text=Structured%20logging%20is%20the%20practice,application%20or%20an%20interested%20individual.
- https://springframework.guru/using-logback-spring-boot/
- https://logback.qos.ch/manual/configuration.html
- https://www.baeldung.com/spring-boot-embedded-tomcat-logs
- https://github.com/akkinoc/logback-access-spring-boot-starter
- https://cassiomolin.com/programming/log-aggregation-with-spring-boot-elastic-stack-and-docker/
