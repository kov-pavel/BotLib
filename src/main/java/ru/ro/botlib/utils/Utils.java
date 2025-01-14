package ru.ro.botlib.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.chat.CustomChat;

import java.util.function.Predicate;

public class Utils {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectWriter OBJECT_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    public static ChiefNotifier CHIEF_NOTIFIER;
    public static AbsSender ABS_SENDER;
    public static Predicate<Long> IS_ADMIN_PREDICATE;

    public static void init(CustomChat errorChat, AbsSender absSender, Predicate<Long> isAdminPredicate) {
        CHIEF_NOTIFIER = new ChiefNotifier(errorChat);
        ABS_SENDER = absSender;
        IS_ADMIN_PREDICATE = isAdminPredicate;
    }
}
