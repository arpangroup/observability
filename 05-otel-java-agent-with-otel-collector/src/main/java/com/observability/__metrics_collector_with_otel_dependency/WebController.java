package com.observability.__metrics_collector_with_otel_dependency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {
    private final Logger log = LoggerFactory.getLogger(WebController.class);
//    Counter visitCounter;

    /*public WebController(MeterRegistry registry) {
        visitCounter = Counter.builder("visit_counter")
                .description("Number of visits to the site")
                .register(registry);
    }*/

    @GetMapping("/")
    public String index() {
        log.info("#############Inside index.......");
//        visitCounter.increment();
        return "Hello World!";
    }


    @GetMapping("/api/{orderId}")
    public String getOrderDetails(@PathVariable String orderId) {
        log.info("############# Inside getOrderDetails {}.......", orderId);
//        visitCounter.increment();
        return "orderDetails for " + orderId;
    }
}
