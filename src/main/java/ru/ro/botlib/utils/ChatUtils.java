package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.ro.botlib.exception.BotException;

import java.util.List;

@Slf4j
public class ChatUtils {

    public static boolean isPrivateChat(Chat chat) {
        return chat.getType().equals("private");
    }

    public static boolean isUtilitaryChat(Update update, List<Long> utilitaryChats) {
        log.info("Проверка чата на утилитность, START");

        long chatId = 0;

        try {
            if (update.hasMessage()) {
                chatId = update.getMessage().getChatId();
                log.info("ChatID получен из Message: {}", chatId);
            } else if (update.hasChatMember()) {
                chatId = update.getChatMember().getChat().getId();
                log.info("ChatID получен из ChatMember: {}", chatId);
            } else {
                log.info("Не могу получить ChatID! Завершаю проверку...");
                return false;
            }

            long finalChatId = chatId;
            return utilitaryChats.stream()
                    .anyMatch(utilitaryChat -> utilitaryChat.equals(finalChatId));
        } catch (Exception ex) {
            var errorMsg = String.format("Возникла ошибка при проверки чата (chatID = %d) на утилитность!", chatId);
            throw new BotException(errorMsg, ex);
        } finally {
            log.info("Проверка чата на утилитность, END");
        }
    }
}
