package io.github.dhneio.heka.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.dhneio.heka.client.Protobuf.Field;

import java.io.IOException;

public class FieldValueTypeSerializer extends JsonSerializer<Field.ValueType> {
    @Override
    public void serialize(Field.ValueType valueType, JsonGenerator jGen, SerializerProvider sp)
            throws IOException, JsonProcessingException {
        jGen.writeString(valueType.name());
    }
}
