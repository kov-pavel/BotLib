package ru.ro.botlib.utils.log;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

public class CustomSerializer extends StdSerializer<Object> {

    private final JsonSerializer<Object> defaultSerializer;

    public CustomSerializer(JsonSerializer<Object> defaultSerializer) {
        super(Object.class);
        this.defaultSerializer = defaultSerializer;
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        if (value instanceof Serializable) {
            provider.defaultSerializeValue(value, gen);
        } else {
            serializeClassInfo(value, gen);
        }
    }

    private void serializeClassInfo(Object value, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("class", value.getClass().getName());

        gen.writeObjectFieldStart("fields");
        for (Field field : value.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            gen.writeStringField(field.getName(), field.getType().getSimpleName());
        }
        gen.writeEndObject();

        gen.writeEndObject();
    }
}
