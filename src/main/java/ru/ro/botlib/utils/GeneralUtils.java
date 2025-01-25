package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ro.botlib.exception.BotException;

import java.util.List;
import java.util.Random;

@Slf4j
public class GeneralUtils {

    private static final Random rand = new Random();

    public static <E> E getRandomElem(List<E> l) {
        return l.get(rand.nextInt(l.size()));
    }

    public static String getFullUserName(User user) {
        return getFullUserName(user.getFirstName(), user.getLastName());
    }

    public static String getFullUserName(String firstName, String lastName) {
        var fullUserNameSb = new StringBuilder();
        if (StringUtils.hasText(firstName)) {
            fullUserNameSb.append(firstName);
        }

        if (StringUtils.hasText(lastName)) {
            if (StringUtils.hasText(firstName)) {
                fullUserNameSb.append(" ");
            }

            fullUserNameSb.append(lastName);
        }

        return fullUserNameSb.toString();
    }

    public static String extractMemberUserName(Update update) {
        var operationName = "Доставание Username из Update";
        log.info("{}, START", operationName);
        try {
            if (update.hasMessage()) {
                log.info("Username получен из Message.");
                return update.getMessage().getFrom().getUserName();
            } else if (update.hasChatMember()) {
                log.info("Username получен из ChatMember.");
                return update.getChatMember().getNewChatMember().getUser().getUserName();
            } else if (update.hasMyChatMember()) {
                log.info("Username получен из MyChatMember.");
                return update.getMyChatMember().getNewChatMember().getUser().getUserName();
            } else {
                throw new BotException(operationName);
            }
        } finally {
            log.info("{}, END", operationName);
        }
    }
}
