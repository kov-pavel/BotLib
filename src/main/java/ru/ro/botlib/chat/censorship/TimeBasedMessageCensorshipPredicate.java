package ru.ro.botlib.chat.censorship;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ro.utils.Utils;

@Component
public class TimeBasedMessageCensorshipPredicate extends CensorshipPredicate {

    public static final int FROM_TIME_MESSAGES_ALLOWED = 8;
    public static final int TO_TIME_MESSAGES_ALLOWED = 23;

    @Override
    public boolean test(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }

        var updateUnixTime = update.getMessage().getDate();
        var updateDate = Utils.unix2date(updateUnixTime);
        var updateHours = updateDate.getHours();

        return updateHours >= TO_TIME_MESSAGES_ALLOWED || updateHours < FROM_TIME_MESSAGES_ALLOWED;
    }
}
