package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        if (update.hasMessage()) {
            return update.getMessage().getFrom().getUserName();
        } else if (update.hasChatMember()) {
            return update.getChatMember().getNewChatMember().getUser().getUserName();
        } else if (update.hasMyChatMember()) {
            return update.getMyChatMember().getNewChatMember().getUser().getUserName();
        } else {
            log.error("Не могу получить UserName из обновления. Возвращаю пустую строку.");
            return "";
        }
    }
}
