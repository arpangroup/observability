package com.observability.sre_logging.core.logging;

import org.slf4j.event.Level;

import java.lang.annotation.*;

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    Level level() default Level.INFO;

    boolean parameters() default true;

    boolean response() default true;

}
