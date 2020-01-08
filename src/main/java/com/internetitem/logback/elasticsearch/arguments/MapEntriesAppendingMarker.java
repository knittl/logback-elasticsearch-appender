package com.internetitem.logback.elasticsearch.arguments;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Marker;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * A {@link Marker} OR {@link StructuredArgument} that appends entries
 * from a {@link Map} into the logging event output.
 * <p>
 *
 * When writing to the JSON data (via {@link ArgumentsJsonProvider} or {@link LogstashMarkersJsonProvider}):
 * <ul>
 * <li>Keys are converted to a {@link String} via {@link String#valueOf(Object)}, and used as field names.</li>
 * <li>Values are converted using an {@link ObjectMapper}.</li>
 * </ul>
 * <p>
 *
 * When writing to a String (when used as a {@link StructuredArgument} to the event's formatted message),
 * {@link String#valueOf(Object)} is used to convert the map to a string.
 * <p>
 *
 * For example, if the message is "mymessage {}", and map argument contains is
 *
 * <pre>
 * {@code
 * name1= a String "value1",
 * name2= an Integer 5,
 * name3= an array containing [1, 2, 3],
 * name4= a map containing  name5=6
 * }
 * </pre>
 * <p>
 * Then the message, name1, name2, name3, name4 fields will be added to the json for the logstash event.
 * <p>
 * For example:
 *
 * <pre>
 * {
 *     "message" : "mymessage [name1=value1,name2=5,name3=[b...,name4=[name5=6]]",
 *     "name1"   : "value1",
 *     "name2"   : 5,
 *     "name3"   : [1, 2, 3],
 *     "name4"   : { "name5" : 6 }
 * }
 * <p>
 *
 * </pre>
 */
@SuppressWarnings("serial")
public class MapEntriesAppendingMarker extends BasicMarker implements StructuredArgument {

    public static final String MARKER_NAME = BasicMarker.MARKER_NAME_PREFIX + "MAP_FIELDS";

    /**
     * The map from which entries will be appended to the logstash json event.
     */
    private final Map<?, ?> map;

    public MapEntriesAppendingMarker(Map<?, ?> map) {
        super(MARKER_NAME);
        this.map = map;
    }

    @Override
    public void writeTo(JsonGenerator generator) throws IOException {
        if (map != null) {
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                generator.writeFieldName(String.valueOf(entry.getKey()));
                generator.writeObject(entry.getValue());
            }
        }
    }

    @Override
    public String toStringSelf() {
        return String.valueOf(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof MapEntriesAppendingMarker)) {
            return false;
        }

        MapEntriesAppendingMarker other = (MapEntriesAppendingMarker) obj;
        return Objects.equals(this.map, other.map);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + super.hashCode();
        result = prime * result + Objects.hashCode(this.map);
        return result;
    }
}
