package ru.ro.botlib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.chat.CustomChat;
import ru.ro.botlib.utils.log.CustomBeanSerializerModifier;

import java.util.function.Predicate;

public class SDKUtils {

    private static final ObjectMapper OBJECT_MAPPER;
    public static final ObjectWriter OBJECT_WRITER;

    public static ChiefNotifier CHIEF_NOTIFIER;
    public static AbsSender ABS_SENDER;
    public static Predicate<Long> IS_ADMIN_PREDICATE;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        //OBJECT_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        //registerCustomSerializer();

        OBJECT_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    }

    private static void registerCustomSerializer() {
        SimpleModule module = new SimpleModule();
        module.setSerializerModifier(new CustomBeanSerializerModifier());
        //module.addSerializer(Object.class, new CustomSerializer());
        OBJECT_MAPPER.registerModule(module);
    }

    public static void init(CustomChat errorChat, AbsSender absSender, Predicate<Long> isAdminPredicate) {
        CHIEF_NOTIFIER = new ChiefNotifier(errorChat);
        ABS_SENDER = absSender;
        IS_ADMIN_PREDICATE = isAdminPredicate;
    }
}
