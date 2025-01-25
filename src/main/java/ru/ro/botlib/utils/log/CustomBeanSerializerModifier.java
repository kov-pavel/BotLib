package ru.ro.botlib.utils.log;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

public class CustomBeanSerializerModifier extends BeanSerializerModifier {

    @Override
    public JsonSerializer<?> modifySerializer(
            SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {

        if (beanDesc.getBeanClass().equals(Object.class)) {
            return new CustomSerializer((JsonSerializer<Object>) serializer);
        }

        return serializer;
    }
}
