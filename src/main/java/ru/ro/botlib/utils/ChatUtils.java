package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.exception.BotException;
import ru.ro.botlib.utils.log.LogUtils;

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

    public static void sendMessage(long chatId, String text, AbsSender absSender) {
        var sendMsg = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();
        sendMessage(sendMsg, absSender);
    }

    public static void sendMessage(long chatId, int threadId, String text, AbsSender absSender) {
        var sendMsg = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .messageThreadId(threadId)
                .build();
        sendMessage(sendMsg, absSender);
    }

    public static void sendMessage(SendMessage sendMessage, AbsSender absSender) {
        var operationName = String.format("Отправка сообщения %s", LogUtils.parseObjectForLog(sendMessage));

        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException ex) {
            throw new BotException(operationName, ex);
        }
    }
}
