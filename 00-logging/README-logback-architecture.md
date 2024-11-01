
## [What is a Logback Appender?](https://logback.qos.ch/manual/appenders.html)
The three main components in Logback architecture are: Logger, Appender, and Layout. In very simple terms:

Logback delegates the task of writing a logging event to components called appenders. Appenders must implement the `ch.qos.logback.core.Appender` interface.
> A `Logger` is an interface for creating log messages. An `Appender` sends log messages to a target, and a Logger can have several appenders. Finally, As it turns out, a `Layout` is responsible for formatting the log message before it is sent to the target.

<img src="logback_appender.png"/>

````java
public interface Appender<E> extends LifeCycle, ContextAware, FilterAttachable { 
    public String getName();
    public void setName(String name);
    void doAppend(E event);
}
````


## [AppenderBase:](https://logback.qos.ch/manual/appenders.html#AppenderBase)

````java
public synchronized void doAppend(E eventObject) {
    // prevent re-entry.
    if (guard) {
        return;
    }

    try {
        guard = true;

        if (!this.started) {
            if (statusRepeatCount++ < ALLOWED_REPEATS) {
                addStatus(new WarnStatus(
                        "Attempted to append to non started appender [" + name + "].",this));
            }
            return;
        }

        if (getFilterChainDecision(eventObject) == FilterReply.DENY) {
            return;
        }

        // ok, we now invoke the derived class's implementation of append
        this.append(eventObject);

    } finally {
        guard = false;
    }
}
````
## [OutputStreamAppender](https://logback.qos.ch/manual/appenders.html#OutputStreamAppender)
`OutputStreamAppender` appends events to a `java.io.OutputStream`. This class provides basic services that other appenders build upon.

Users do not usually instantiate `OutputStreamAppender` objects directly, since in general the `java.io.OutputStream` type cannot be conveniently mapped to a string

The `OutputStreamAppender` is the super-class of three other appenders, namely `ConsoleAppender`, `FileAppender` which in turn is the super class of `RollingFileAppender`.

<img src="appenderClassDiagram.jpg"/>


## [ConsoleAppender](https://logback.qos.ch/manual/appenders.html#ConsoleAppender)
````xml
<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
    <encoder>
      <pattern>%-4relative [%thread] %-5level %logger{35} -%kvp- %msg %n</pattern>
    </encoder>
  </appender>

  <root level="DEBUG">
    <appender-ref ref="STDOUT" />
  </root>
  </configuration>
````

## [FileAppender](https://logback.qos.ch/manual/appenders.html#FileAppender)
````xml
<configuration>

    <!-- Insert the current time formatted as "yyyyMMdd'T'HHmmss" under
         the key "bySecond" into the logger context. This value will be
         available to all subsequent configuration elements. -->
    <timestamp key="bySecond" datePattern="yyyyMMdd'T'HHmmss"/>
    
    <appender name="FILE" class="ch.qos.logback.core.FileAppender">
        <!-- use the previously created timestamp to create a uniquely named log file -->
        <file>testFile-${bySecond}.log</file>
        <append>true</append>
        <!-- set immediateFlush to false for much higher logging throughput -->
        <immediateFlush>true</immediateFlush>
        <!-- encoders are assigned the type
            ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%-4relative [%thread] %-5level %logger{35} -%kvp- %msg%n</pattern>
        </encoder>
    </appender>

    <root level="DEBUG">
        <appender-ref ref="FILE" />
    </root>
</configuration>
````

## Logback TCP appender
````xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STASH" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
        <destination>YOUR-IP:PORT</destination>
        <ringBufferSize>1024</ringBufferSize>
        <keepAliveDuration>5 minutes</keepAliveDuration>
        <!-- encoder is required -->
        <!-- <encoder class="net.logstash.logback.encoder.LogstashEncoder" /> -->
        <encoder class="net.logstash.logback.encoder.LogstashEncoder" >
            <customFields>{
                "app":"",
                "es_id":"",
                "es_ver":"0.02",
                "api": "",
                "env_name":"",
                "app_id":"",
                "category":"app"
                }
            </customFields>
        </encoder>

        <ssl>
            <trustStore>
                <location>file:/etc/esaas/keystore/esaas-keystore.jks</location>
                <password>****</password>
            </trustStore>
            <keyStore>
                <location>file:/etc/esaas/keystore/esaas-keystore.jks</location>
                <password>****</password>
            </keyStore>
        </ssl>

    </appender>

    <logger name="org.hibernate" level="TRACE">
        <appender-ref ref="STASH"/>
    </logger>

    <root level="INFO">
        <appender-ref ref="STDOUT"/>
        <appender-ref ref="STASH"/>
    </root>
</configuration>
````


## Other Appenders:
- [SMTPAppender](https://logback.qos.ch/manual/appenders.html#SMTPAppender)
- [DBAppender](https://logback.qos.ch/manual/appenders.html#DBAppender)
- [AsyncAppender](https://logback.qos.ch/manual/appenders.html#AsyncAppender): AsyncAppender buffers events in a `BlockingQueue`. A worker thread created by `AsyncAppender` takes events from the head of the queue, and dispatches them to the single appender attached to `AsyncAppender`.
-

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
