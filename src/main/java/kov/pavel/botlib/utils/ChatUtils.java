package kov.pavel.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
public class ChatUtils {

    public static boolean isPrivateChat(Chat chat) {
        return chat.getType().equals("private");
    }

    public static boolean isUtilitaryChat(Update update, List<Long> utilitaryChats) {
        Long chatId;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            log.info("ChatID получен из Message: " + chatId);
        } else if (update.hasChatMember()) {
            chatId = update.getChatMember().getChat().getId();
            log.info("ChatID получен из ChatMember: " + chatId);
        } else {
            log.info("Не могу получить ChatID! Завершаю проверку...");
            return false;
        }

        return utilitaryChats.stream()
                .anyMatch(utilitaryChat -> utilitaryChat.equals(chatId));
    }
}
