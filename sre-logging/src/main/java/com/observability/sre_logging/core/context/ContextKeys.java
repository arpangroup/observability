package com.observability.sre_logging.core.context;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ContextKeys {
    public static final String REMOTE_APPLICATION_ID = "remote-application-id";
    public static final String REMOTE_HOST = "remote-host";
    public static final String REMOTE_APPLICATION_MODULE = "remote-application-name";
    public static final String REMOTE_EXECUTION_TIMESTAMP = "remote-execution-timestamp";
    public static final String REMOTE_PUBLISH_TIMESTAMP = "remote-publish-timestamp";
    public static final String REMOTE_EVENT_ID = "remote-event-id";
    public static final String CHANNEL_TYPE = "channel-type";
    public static final String REMOTE_EVENT_TYPE = "remote-event-type";
    public static final String REMOTE_EVENT_VERSION = "remote-event-version";
    public static final String TRACE_ID = "trace-id";
    public static final String CORRELATION_ID = "correlation-id";
    public static final String SESSION_ID = "session-id";
    public static final String USER_AGENT = "user-agent";
    public static final String USER = "user";
    public static final String ERROR_CODE = "error-code";
    public static final String ERROR_CATEGORY = "error-category";
    public static final String ERROR_MESSAGE = "error-message";
    public static final String ERROR_CODE_DIGITAL = "ercd";
    public static final String APPLICATION_ID = "application-id";
    public static final String APPLICATION_MODULE = "application-name";
    public static final String API_CLIENT_ID = "api-clientid";
    public static final String API_SIGNATURE = "api-signature";
    public static final String API_SIGNATURE_URL = "api-signatureUrl";
    public static final String DP_OWNER = "dp-owner";
    public static final String SPAN_ID = "span=id";
    public static final String PARENT_SPAN_ID = "parent-span-id";
    public static final String PATH_PARAMS = "path-params";
    public static final String QUERY_PARAMS = "query-params";
    public static final String SESSION_CACHE = "sessioncache";
    public static final String SESSION_CACHE_KEY = "key";
    public static final String SESSION_CACHE_COOKIE_NM = "cookie_nm";
    public static final String SESSION_CACHE_COOKIE_VAL = "cookie_val";
    public static final String SESSION_CACHE_ADDR = "addr";
    public static final String EVENT_START = "eventStart";
    public static final String DESTINATION_HOST = "destHost";
    public static final String DESTINATION_PATH = "destPath";
    public static final String PROTOCOL = "protocol";
    public static final String RESPONSE_TIME = "time";

    private static final Set<String> MDC_KEYS = new HashSet<>(Arrays.asList(EVENT_START, DESTINATION_HOST, DESTINATION_PATH, PROTOCOL, RESPONSE_TIME));

    public ContextKeys() {
    }
}
