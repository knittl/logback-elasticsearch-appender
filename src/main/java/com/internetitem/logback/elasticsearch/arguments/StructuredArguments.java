package com.internetitem.logback.elasticsearch.arguments;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.status.WarnStatus;
import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

public final class StructuredArguments {

    /**
     * The default message format used when writing key value pairs to the log message.
     */
    public static final String DEFAULT_KEY_VALUE_FORMAT_PATTERN = "{0}={1}";

    /**
     * A message format pattern that will only write the argument value to a log message (i.e. it won't write the key).
     */
    public static final String VALUE_ONLY_FORMAT_PATTERN = "{1}";

    private StructuredArguments() {
    }

    /**
     * Adds "key":"value" to the JSON event AND value to the formatted message (without the key).
     *
     * @see ObjectAppendingMarker
     * @see #VALUE_ONLY_FORMAT_PATTERN
     */
    public static StructuredArgument value(String key, Object value) {
        return keyValue(key, value, VALUE_ONLY_FORMAT_PATTERN);
    }

    /**
     * Adds "key":"value" to the JSON event AND name/value to the formatted message using the given
     * messageFormatPattern.
     *
     * @see ObjectAppendingMarker
     */
    public static StructuredArgument keyValue(String key, Object value, String messageFormatPattern) {
        return new ObjectAppendingMarker(key, value, messageFormatPattern);
    }

    /**
     * Convenience method for calling {@link #keyValue(String, Object, String)} using the
     * {@link #DEFAULT_KEY_VALUE_FORMAT_PATTERN}.
     * <p>
     * Basically, adds "key":"value" to the JSON event AND name=value to the formatted message.
     *
     * @see ObjectAppendingMarker
     * @see #DEFAULT_KEY_VALUE_FORMAT_PATTERN
     */
    public static StructuredArgument keyValue(String key, Object value) {
        return keyValue(key, value, DEFAULT_KEY_VALUE_FORMAT_PATTERN);
    }

    /**
     * Adds a "key":"value" entry for each Map entry to the JSON event AND map.toString() to the formatted message.
     *
     * @see MapEntriesAppendingMarker
     */
    public static StructuredArgument entries(Map<?, ?> map) {
        return new MapEntriesAppendingMarker(map);
    }

    /**
     * Format the argument into a string.
     * <p>
     * This method mimics the slf4j behaviour: array objects are formatted as array using {@link Arrays#toString},
     * non array object using {@link String#valueOf}.
     * <p>
     *
     * see org.slf4j.helpers.MessageFormatter#deeplyAppendParameter(StringBuilder, Object, Map)}.
     */
    public static String toString(Object arg) {

        if (arg == null) {
            return "null";
        }

        Class argClass = arg.getClass();

        try {
            if (!argClass.isArray()) {
                return String.valueOf(arg);
            } else {

                if (argClass == byte[].class) {
                    return Arrays.toString((byte[]) arg);
                } else if (argClass == short[].class) {
                    return Arrays.toString((short[]) arg);
                } else if (argClass == int[].class) {
                    return Arrays.toString((int[]) arg);
                } else if (argClass == long[].class) {
                    return Arrays.toString((long[]) arg);
                } else if (argClass == char[].class) {
                    return Arrays.toString((char[]) arg);
                } else if (argClass == float[].class) {
                    return Arrays.toString((float[]) arg);
                } else if (argClass == double[].class) {
                    return Arrays.toString((double[]) arg);
                } else if (argClass == boolean[].class) {
                    return Arrays.toString((boolean[]) arg);
                } else {
                    return Arrays.deepToString((Object[]) arg);
                }
            }

        } catch (Exception ex) {
            ILoggerFactory loggerFactory = LoggerFactory.getILoggerFactory();
            if (loggerFactory instanceof Context) {
                Context context = (Context) loggerFactory;
                StatusManager statusManager = context.getStatusManager();
                statusManager.add(new WarnStatus(
                        "Failed toString() invocation on an object of type [" + argClass.getName() + "]",
                        StructuredArguments.class,
                        ex));
            } else {
                System.err.println("Failed toString() invocation on an object of type [" + argClass.getName() + "]");
                ex.printStackTrace();
            }
            return "[FAILED toString()]";
        }
    }
}
