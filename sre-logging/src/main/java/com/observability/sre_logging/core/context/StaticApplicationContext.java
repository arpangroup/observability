package com.observability.sre_logging.core.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;

public class StaticApplicationContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private static void setApplicatonContext(ApplicationContext applicationContext) {
        StaticApplicationContext.applicationContext = applicationContext;
    }

    public static boolean isInit() {
        return applicationContext != null;
    }

    public static Optional<ApplicationContext> optional() {
        return Optional.ofNullable(getApplicationContext());
    }


    public static ApplicationContext getApplicationContext() {
        return StaticApplicationContext.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        StaticApplicationContext.setApplicatonContext(applicationContext);
    }
}
