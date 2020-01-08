package com.internetitem.logback.elasticsearch.arguments;

import org.slf4j.Logger;

/**
 * A wrapper for an argument passed to a log method (e.g. {@link Logger#info(String, Object...)})
 * that adds data to the JSON event (via {@link ArgumentsJsonProvider}).
 */
public interface StructuredArgument extends WritableMarker {
}