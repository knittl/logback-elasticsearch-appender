package com.internetitem.logback.elasticsearch.arguments;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * A cache for {@link MessageFormat} objects.
 * <p>
 * 
 * Since only a small subset of {@link MessageFormat}s are generally used by
 * {@link SingleFieldAppendingMarker}, the {@link MessageFormatCache} will
 * cache them (per thread) so that they can be reused.
 * <p>
 * 
 * This is a performance optimization to save {@link MessageFormat} construction and parsing time
 * for each argument/marker.
 *  
 */
public class MessageFormatCache {
    
    public static final MessageFormatCache INSTANCE = new MessageFormatCache();
    
    /**
     * Use a {@link ThreadLocal} cache, since {@link MessageFormat}s are not threadsafe.
     */
    private final ThreadLocal<Map<String, MessageFormat>> messageFormats = new ThreadLocal<Map<String, MessageFormat>>() {
        protected Map<String,MessageFormat> initialValue() {
            return new HashMap<>();
        }
    };
    
    public MessageFormat getMessageFormat(String formatPattern) {
        Map<String, MessageFormat> messageFormatsForCurrentThread = messageFormats.get();
        MessageFormat messageFormat = messageFormatsForCurrentThread.get(formatPattern);
        if (messageFormat == null) {
            messageFormat = new MessageFormat(formatPattern);
            messageFormatsForCurrentThread.put(formatPattern, messageFormat);
        }
        return messageFormat;
    }
}
