package com.observability.__metrics_collector_with_otel_dependency;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OtelCollectorApp {

	public static void main(String[] args) {
		SpringApplication.run(OtelCollectorApp.class, args);
	}

}
