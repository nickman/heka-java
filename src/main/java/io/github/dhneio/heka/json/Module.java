package io.github.dhneio.heka.json;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.dhneio.heka.client.Protobuf.Field;

class Module extends SimpleModule {
    public void setupModule(SetupContext context) {
        context.setMixInAnnotations(Field.class, FieldMixIn.class);
    }
}
