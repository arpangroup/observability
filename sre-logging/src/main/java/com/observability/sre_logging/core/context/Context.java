package com.observability.sre_logging.core.context;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.slf4j.MDC;

public class Context {
    private static final ThreadLocal<Context> requestScopedContext = ThreadLocal.withInitial(Context::new);

    private final Map<String, Object> attributes = new ConcurrentHashMap<>();
    private final Set<String> mdcKeys = Collections.newSetFromMap(new ConcurrentHashMap<>(2));

    /**
     * @return Gets the context that is scoped to the current thread.
     */
    public static Context getScopedContext() { return requestScopedContext.get(); }

    public static void setScopedContext(final Context context) {
        if (context == null) {
            clearScopedContext();
            return;
        }
        requestScopedContext.set(context);
    }

    public static void clearScopedContext() { requestScopedContext.remove(); }

    public Object get(String key) { return attributes.get(key); }

    public <T> Object getOrDefault(String key, T defaultValue) { return attributes.getOrDefault(key, defaultValue); }

    public Object computeIfAbsent(String key, Function<String, Object> mappingFunction) {
        return attributes.computeIfAbsent(key, mappingFunction);
    }

    /**
     * Stores an arbitary object in the {@link Context}
     *
     * @param key   Key to store the value with
     * @param value Value to store
     */
    public void put(String key, Object value) {
        if (key != null && value != null) {
            attributes.put(key, value);
        }
    }

    /**
     * Stores a String value in the {@link Context} and optionally the {@link MDC}
     * @param key   Key to store the value with
     * @param value Value to store
     * @param mdc   if true will store in {@link MDC}
     */
    public void put(String key, String value, boolean mdc) {
        put(key, value);
        if (mdc && key != null && value != null) {
            mdcKeys.add(key);
        }
    }

    /**
     * Convenience method to put a key/value into the context as wel as the MDC
     * equivalent to {@link #put(String, String, boolean)} where boolean MDC is true
     *
     * @param key
     * @param value
     */
    public void putMDC(String key, String value) {
        put(key, value, true);
    }

    public Set<String> getMdcKeys() {
        return Collections.unmodifiableSet(mdcKeys);
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    /**
     * Clear the context and remove any keys that were added to the {@link MDC}
     */
    public void clear() {
        attributes.clear();
        mdcKeys.clear();
    }

    public String getString(String key) {
        Object obj = get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        return obj.toString();
    }

    public void markAsMdc(String key) {
        mdcKeys.add(key);
    }

    public void remove(String key) {
        attributes.remove(key);
        if (mdcKeys.contains(key)) {
            mdcKeys.remove(key);
        }
    }

    public boolean hasKey(String key) {
        return key != null && (attributes.containsKey(key) || mdcKeys.contains(key));
    }
}
