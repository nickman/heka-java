package io.github.dhneio.heka.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.ByteString;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class Base64Serializer extends JsonSerializer<ByteString> {
    @Override
    public void serialize(ByteString bytes, JsonGenerator jGen, SerializerProvider sp)
            throws IOException, JsonProcessingException {
        jGen.writeString(DatatypeConverter.printBase64Binary(bytes.toByteArray()));
    }
}
