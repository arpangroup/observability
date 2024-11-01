package com.observability.sre_logging.core.interceptor;

import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;

import java.util.Map;

public class KafkaDefaultMDCValuesInterceptor implements ConsumerInterceptor {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(KafkaDefaultMDCValuesInterceptor.class);
    private Map<String, String> kafkaDefaultMDCValuesMap;
    private static final String KAFKA_DEFAULT_MDC_VALUES = "photon.logging.kafka.default-mdc-values";

    @Override
    public ConsumerRecords onConsume (ConsumerRecords consumerRecords) {
        if (!StringUtils.isEmpty(kafkaDefaultMDCValuesMap)) {
            kafkaDefaultMDCValuesMap.forEach (MDC:: put);
        }
        return consumerRecords;
    }


    @Override
    public void onCommit (Map offsets) {
        this.kafkaDefaultMDCValuesMap.keySet().forEach (MDC::remove);
    }

    @Override
    public void close() {
    }

    @Override
    public void configure (Map<String, ?> map) {
        this.kafkaDefaultMDCValuesMap = (Map<String, String>) map.get(KAFKA_DEFAULT_MDC_VALUES);
    }

}
