package com.internetitem.logback.elasticsearch.arguments;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Marker;

import com.fasterxml.jackson.core.JsonGenerator;

/**
 * A {@link Marker} OR {@link StructuredArgument} that appends
 * a single field into the JSON event.
 * <p>
 *
 * When writing to the JSON data (via {@link ArgumentsJsonProvider} or {@link LogstashMarkersJsonProvider}):
 * <ul>
 * <li>Field names are written via {@link #writeFieldName(JsonGenerator)},
 *     which just uses {@link #fieldName} is used as the field name</li>
 * <li>Values are written via {@link #writeFieldValue(JsonGenerator)},
 *     which subclasses must override</li>
 * </ul>
 * <p>
 *
 * When writing to a String (when used as a {@link StructuredArgument} to the event's formatted message),
 * the {@link #messageFormatPattern} is used to construct the string output.
 * {@link #getFieldName()} will be substituted in {0} in the {@link #messageFormatPattern}.
 * {@link #getFieldValue()} will be substituted in {1} in the {@link #messageFormatPattern}.
 * Subclasses must override {@link #getFieldValue()} to provide the field value to include.
 */
@SuppressWarnings("serial")
public abstract class SingleFieldAppendingMarker extends BasicMarker implements StructuredArgument {

    public static final String MARKER_NAME_PREFIX = BasicMarker.MARKER_NAME_PREFIX + "APPEND_";

    /**
     * Name of the field to append.
     *
     * Note that the value of the field is provided by subclasses via {@link #writeFieldValue(JsonGenerator)}.
     */
    private final String fieldName;

    /**
     * Pattern to use when appending the field/value in {@link #toString()}.
     * <p>
     * {@link #getFieldName()} will be substituted in {0}.
     * {@link #getFieldValue()} will be substituted in {1}.
     */
    private final String messageFormatPattern;

    public SingleFieldAppendingMarker(String markerName, String fieldName, String messageFormatPattern) {
        super(markerName);
        this.fieldName = Objects.requireNonNull(fieldName, "fieldName must not be null");
        this.messageFormatPattern = Objects.requireNonNull(messageFormatPattern, "messageFormatPattern must not be null");
    }

    public String getFieldName() {
        return fieldName;
    }

    public void writeTo(JsonGenerator generator) throws IOException {
        writeFieldName(generator);
        writeFieldValue(generator);
    }

    /**
     * Writes the field name to the generator.
     */
    protected void writeFieldName(JsonGenerator generator) throws IOException {
        generator.writeFieldName(getFieldName());
    }

    /**
     * Writes the field value to the generator.
     */
    protected abstract void writeFieldValue(JsonGenerator generator) throws IOException;

    @Override
    public String toStringSelf() {
        final String fieldValueString = StructuredArguments.toString(getFieldValue());
        /*
         * Optimize for commonly used messageFormatPattern
         */
        if (StructuredArguments.VALUE_ONLY_FORMAT_PATTERN.equals(messageFormatPattern)) {
            return fieldValueString;
        }

        if (StructuredArguments.DEFAULT_KEY_VALUE_FORMAT_PATTERN.equals(messageFormatPattern)) {
            return getFieldName()
                    + "="
                    + fieldValueString;
        }

        /*
         * Custom messageFormatPattern
         */
        return MessageFormatCache.INSTANCE.getMessageFormat(this.messageFormatPattern)
                .format(new Object[] {getFieldName(), fieldValueString});
    }

    /**
     * Return the value that should be included in the output of {@link #toString()}.
     */
    public abstract Object getFieldValue();

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof SingleFieldAppendingMarker)) {
            return false;
        }

        SingleFieldAppendingMarker other = (SingleFieldAppendingMarker) obj;
        return this.fieldName.equals(other.fieldName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + this.fieldName.hashCode();
        return result;
    }
}
