package com.observability.sre_logging.core.context;

import jakarta.servlet.http.HttpServletRequest;


public class DigitalApiStandardsHttpRequestExtractor implements HttpRequestExtractor {
    org.slf4j.Logger log =  org.slf4j.LoggerFactory.getLogger(DigitalApiStandardsHttpRequestExtractor.class);

    @Override
    public void extract(Context context, HttpServletRequest servletRequest) {

    }

}
