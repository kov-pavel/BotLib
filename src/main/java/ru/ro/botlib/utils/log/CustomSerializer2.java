package ru.ro.botlib.utils.log;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.CacheProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.impl.WritableObjectId;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;

public class CustomSerializer2 extends StdSerializer<Object> {
    private static final ObjectMapper defaultMapper = new ObjectMapper();
    private static final int MAX_DEPTH = 3;

    public CustomSerializer2() {
        super(Object.class);
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        serializeWithDepth(value, gen, provider, 1);
    }

    private void serializeWithDepth(Object value, JsonGenerator gen,
                                    SerializerProvider provider, int depth)
            throws IOException {

        if (depth > MAX_DEPTH) {
            gen.writeString("[MAX DEPTH REACHED]");
            return;
        }

        if (value instanceof Serializable) {
            serializeSerializable(value, gen, provider, depth);
        } else {
            serializeClassInfo(value, gen, depth);
        }
    }

    private void serializeSerializable(Object value, JsonGenerator gen,
                                       SerializerProvider provider, int depth)
            throws IOException {

        BeanDescription desc = provider.getConfig().introspect(provider.constructType(value.getClass()));
        JsonSerializer<Object> serializer = provider.findValueSerializer(value.getClass());

        serializer.serialize(value, gen, new SerializerProviderWrapper(provider, depth));
    }

    private void serializeClassInfo(Object value, JsonGenerator gen, int depth)
            throws IOException {

        gen.writeStartObject();
        gen.writeStringField("class", value.getClass().getName());

        gen.writeObjectFieldStart("fields");
        for (Field field : value.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object fieldValue = field.get(value);

                gen.writeFieldName(field.getName());
                if (depth < MAX_DEPTH) {
                    serializeWithDepth(fieldValue, gen, null, depth + 1);
                } else {
                    gen.writeString(field.getType().getSimpleName());
                }
            } catch (Exception e) {
                gen.writeString("inaccessible");
            }
        }
        gen.writeEndObject();

        gen.writeEndObject();
    }

    private static class SerializerProviderWrapper extends DefaultSerializerProvider {
        private final int currentDepth;
        private final SerializerProvider src;

        public SerializerProviderWrapper(SerializerProvider src, int depth) {
            this.src = src;
            this.currentDepth = depth;
        }

        @Override
        public DefaultSerializerProvider createInstance(SerializationConfig serializationConfig, SerializerFactory serializerFactory) {
            return null;
        }

        @Override
        public DefaultSerializerProvider withCaches(CacheProvider cacheProvider) {
            return null;
        }

        @Override
        public void serializeValue(JsonGenerator gen, Object value) throws IOException {
            if (currentDepth + 1 > MAX_DEPTH) {
                gen.writeString("[MAX DEPTH]");
                return;
            }

            if (value == null) {
                super.serializeValue(gen, null);
            } else {
                findValueSerializer(value.getClass()).serialize(
                        value, gen, new SerializerProviderWrapper(src, currentDepth + 1)
                );
            }
        }
    }
}
