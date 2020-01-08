package com.internetitem.logback.elasticsearch.arguments;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface WritableMarker {
    /**
     * Writes the data associated with this marker to the given {@link JsonGenerator}.
     *
     * @param generator the generator to which to write the output of this marker.
     * @throws IOException if there was an error writing to the generator
     */
    void writeTo(JsonGenerator generator) throws IOException;
}
