
## [Access to Spring ApplicationContext from a custom Logback appender](How to call a Spring Bean from a custom Logback appender class)
After a brief introduction about Logback and Spring Boot logging, let’s dive into the code and see what the root of the problem is. In my case, I have a custom Logback appender (NotificationAppender) that is supposed to send a notification (for example, email, push notification, or…) under certain circumstances (for example, the occurrence of a specific number of error logs or finding a specific text in the logs, or…).

To send that notification, the NotificationAppender needs to access a Spring bean called Notifier, but as we mentioned before, Logback is not managed by the Spring ApplicationContext, so it does not access ApplicationContext, and we can not inject the Notifier bean in our custom appender (NotificationAppender), it means this code will not work:

````java
public class NotificationAppender extends AppenderBase<ILoggingEvent> {
    @Autowired
    private  Notifier notifier;
    @Override
    protected void append(ILoggingEvent loggingEvent) {
        notifier.notify(loggingEvent.getFormattedMessage());
    }
}
````

I registered the NotificationAppender in the logback-spring.xml:
````xml
<configuration debug="true">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
    <appender name="NOTIFY" class="com.saeed.springlogbackappender.NotificationAppender"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="NOTIFY" />
    </root>
</configuration>
````
We will get errors like this because, as I mentioned, Logback does not access the ApplicationContext:
````
10:41:05,660 |-ERROR in com.saeed.springlogbackappender.NotificationAppender[NOTIFY] - Appender [NOTIFY] failed to append. java.lang.NullPointerException: Cannot invoke "com.saeed.springlogbackappender.Notifier.notify(String)" because "this.notifier" is null
 at java.lang.NullPointerException: Cannot invoke "com.saeed.springlogbackappender.Notifier.notify(String)" because "this.notifier" is null
````
Even if we change the appender class and make it a Spring bean by adding @Component on top of the class, The situation is getting worse.
````java
@Component
public class NotificationAppender extends AppenderBase<ILoggingEvent> {
    private  Notifier notifier;
    public NotificationAppender(Notifier notifier) {
        this.notifier = notifier;
    }
    @Override
    protected void append(ILoggingEvent loggingEvent) {
        notifier.notify(loggingEvent.getFormattedMessage());
    }
}
````
And we will get an error similar to this:
````properties
10:53:57,887 |-ERROR in ch.qos.logback.core.model.processor.AppenderModelHandler - Could not create an Appender of type [com.saeed.springlogbackappender.NotificationAppender]. ch.qos.logback.core.util.DynamicClassLoadingException: Failed to instantiate type com.saeed.springlogbackappender.NotificationAppender
Caused by: java.lang.NoSuchMethodException: com.saeed.springlogbackappender.NotificationAppender.<init>()
````

This is because Logback needs a default constructor to initialize the `NotificationAppender`. If we add the default constructor to the class, we will get the previous `NullPointerException` for the Notifier bean because now we have two instances of `NotificationAppender` in our application, one instantiated and managed by `Logback` and the other by `ApplicationContext`!


Now, we want to solve this problem by providing three solutions. I have created a spring boot project called spring-logback-appender in GitHub and created separate commits for each solution.

## 1- Spring Boot creates the bean and adds it as a Logback appender dynamically in @PostConstruct
In this approach, we define the NotificationAppender as a Spring bean, so we can inject every Spring bean into it without a problem. But As we saw in the problem statement before, how do we want to introduce this Spring bean as an appender to Logback? We will do it programmatically using the LoggerContext:
````java
@Component
public class NotificationAppender extends AppenderBase<ILoggingEvent> {
    private final Notifier notifier;
    public NotificationAppender(Notifier notifier) {
        this.notifier = notifier;
    }
    @Override
    protected void append(ILoggingEvent loggingEvent) {
        notifier.notify(loggingEvent.getFormattedMessage());
    }
    @PostConstruct
    public void init() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.addAppender(this);
        setContext(context);
        start();
    }
}
````
This will work, and if we call the `/hello` API, we will see that the `Notifier` will notify using the appender.

For me, this approach has some drawbacks:
- It is less flexible because the appender is not configurable in the logback-spring.xml file.
- We will miss some logs at the early stage of the Spring boot start.

## 2- Logback creates the appender and then fills the bean dependency in the custom appender using the ApplicationContexAware
In this approach, to fix one important drawback of the first approach, we will register the Logback appender in a standard way by adding it to the logback-spring.xml file.
````xml
<configuration debug="true">
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    <include resource="org/springframework/boot/logging/logback/console-appender.xml" />
    <appender name="NOTIFY" class="com.saeed.springlogbackappender.NotificationAppender"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="NOTIFY" />
    </root>
</configuration>
````

The other change that we need to make is to make the Notifier field static and `NotificationAppender` implement the ApplicationContextAware:
````java
@Component
public class NotificationAppender extends AppenderBase<ILoggingEvent> implements ApplicationContextAware {
    private static Notifier notifier;
    @Override
    protected void append(ILoggingEvent loggingEvent) {
        if (notifier != null)
            notifier.notify(loggingEvent.getFormattedMessage());
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        notifier = applicationContext.getAutowireCapableBeanFactory().getBean(Notifier.class);
    }
}
````
This approach will result in a similar result to the first approach, but now the appender is configurable using a standard method in the Logback.

We still have some drawbacks to this approach:
- We need to check if the notifier is not null and make it static.
- Since the injected class is not present before the Spring ApplicationContext loads completely, we will miss some logs at an early stage of the application start.

## 3- As mentioned above, you might be interested in not losing the events logged during the Spring Boot startup
In the third and last approach, we will concentrate on fixing the problem of missing logs in the early stages of the application start. For this approach, I was inspired by [this question](https://stackoverflow.com/questions/42859896/dependency-injection-into-logback-appenders-with-spring-boot) on StackOverflow to mix both previous approaches and create a new AppenderDelegator class.

In this approach, we will define two appenders:
- `AppenderDelegator`: Register as an appender in the Logback config file (logback-spring.xml). This appender is our main appender, acts as a `delegator`, and has a buffer to store log events for when the actual logger is not yet ready to log.

````java
public class AppenderDelegator<E> extends UnsynchronizedAppenderBase<E> {
    private final ArrayList<E> logBuffer = new ArrayList<>(1024);
    private Appender<E> delegate;
    @Override
    protected void append(E event) {
        synchronized (logBuffer) {
            if (delegate != null) {
                delegate.doAppend(event);
            } else {
                logBuffer.add(event);
            }
        }
    }
    public void setDelegateAndReplayBuffer(Appender<E> delegate) {
        synchronized (logBuffer) {
            this.delegate = delegate;
            for (E event : this.logBuffer) {
                delegate.doAppend(event);
            }
            this.logBuffer.clear();
        }
    }
}
````
- NotificationAppender: This is our actual appender, which is configured programmatically and uses the Spring SmartLifecycle to have more control over its lifecycle. We will connect this appender to the delegator one during the component start lifecycle:

````java
@Component
public class NotificationAppender extends AppenderBase<ILoggingEvent> implements SmartLifecycle {
    private final Notifier notifier;
    public NotificationAppender(Notifier notifier) {
        this.notifier = notifier;
    }
    @Override
    protected void append(ILoggingEvent loggingEvent) {
        notifier.notify(loggingEvent.getFormattedMessage());
    }
    @Override
    public boolean isRunning() {
        return isStarted();
    }
    @Override
    public void start() {
        super.start();
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        AppenderDelegator<ILoggingEvent> delegate = (AppenderDelegator<ILoggingEvent>) rootLogger.getAppender("DELEGATOR");
        delegate.setDelegateAndReplayBuffer(this);
    }
}
````

