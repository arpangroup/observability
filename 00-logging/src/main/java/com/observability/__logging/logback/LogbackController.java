package com.observability.__logging.logback;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logback")
public class LogbackController {
    private final Logger log = LoggerFactory.getLogger(LogbackController.class);

    @GetMapping
    public String sayHello() {
        log.error("error log");
        log.warn("warn log");
        log.info("info log");
        log.debug("debug log");
        log.trace("trace log");
        return "Hello World!";
    }
}
