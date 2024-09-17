package com.observability.__metrics_collector_with_prometheus.observability;

import com.observability.__metrics_collector_with_prometheus.service.BookService;
import com.observability.__metrics_collector_with_prometheus.service.DefaultBooksService;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObservationConfig {

    @Autowired
    private ObservationRegistry observationRegistry;

    @Autowired
    private MeterRegistry meterRegistry;

    @Bean(name = "bookService")
    public BookService bookService() {
        return new ObservedBookService(new DefaultBooksService(), observationRegistry, meterRegistry);
    }
}
