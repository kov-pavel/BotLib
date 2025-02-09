package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.exception.BotException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class ChatUtils {

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

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

    public static CompletableFuture<Message> sendMessageAsync(long chatId, String text, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> sendMessageSync(chatId, text, absSender));
    }

    public static Message sendMessageSync(long chatId, String text, AbsSender absSender) {
        var sendMsg = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .build();

        return sendMessageSync(sendMsg, absSender);
    }

    public static CompletableFuture<Message> sendMessageAsync(long chatId, int threadId, String text, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> sendMessageSync(chatId, threadId, text, absSender));
    }

    public static Message sendMessageSync(long chatId, int threadId, String text, AbsSender absSender) {
        var sendMsg = SendMessage.builder()
                .text(text)
                .chatId(chatId)
                .messageThreadId(threadId)
                .build();

        return sendMessageSync(sendMsg, absSender);
    }

    private static CompletableFuture<Message> sendMessageAsync(SendMessage sendMessage, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> sendMessageSync(sendMessage, absSender));
    }

    private static Message sendMessageSync(SendMessage sendMessage, AbsSender absSender) {
        var operationName = String.format("Отправка сообщения в чат (chatId = %s)", sendMessage.getChatId());

        try {
            return absSender.execute(sendMessage);
        } catch (TelegramApiException ex) {
            throw new BotException(operationName, ex);
        }
    }

    public static CompletableFuture<Message> sendDocumentAsync(InputFile document, long chatId, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> sendDocumentSync(document, chatId, absSender));
    }

    public static Message sendDocumentSync(InputFile document, long chatId, AbsSender absSender) {
        var operationName = String.format("Отправка документа в чат (chatId = %d)", chatId);

        try {
            var sendDocument = SendDocument.builder()
                    .document(document)
                    .chatId(chatId)
                    .build();
            return absSender.execute(sendDocument);
        } catch (TelegramApiException ex) {
            throw new BotException(operationName, ex);
        }
    }

    public static CompletableFuture<Message> editMsgAsync(Message msg, String text, AbsSender absSender) {
        return editMsgAsync(msg.getChat(), msg, text, absSender);
    }

    public static CompletableFuture<Message> editMsgAsync(Chat chat, Message msg, String text, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> editMsgSync(chat, msg, text, absSender));
    }

    public static Message editMsgSync(Message msg, String text, AbsSender absSender) {
        return editMsgSync(msg.getChat(), msg, text, absSender);
    }

    public static Message editMsgSync(Chat chat, Message msg, String text, AbsSender absSender) {
        var operationName = String.format("Изменения сообщения (msgId = %d) в чате (chatId = %d)", msg.getMessageId(), chat.getId());

        try {
            var editMsg = EditMessageText.builder()
                    .chatId(chat.getId())
                    .messageId(msg.getMessageId())
                    .text(text)
                    .build();
            absSender.execute(editMsg);
            return msg;
        } catch (TelegramApiException ex) {
            throw new BotException(operationName, ex);
        }
    }

    public static CompletableFuture<Message> deleteMsgAsync(Message msg, AbsSender absSender) {
        return deleteMsgAsync(msg.getChat(), msg, absSender);
    }

    public static CompletableFuture<Message> deleteMsgAsync(Chat chat, Message msg, AbsSender absSender) {
        return CompletableFuture.supplyAsync(() -> deleteMsgSync(chat, msg, absSender));
    }

    public static Message deleteMsgSync(Message msg, AbsSender absSender) {
        return deleteMsgSync(msg.getChat(), msg, absSender);
    }

    public static Message deleteMsgSync(Chat chat, Message msg, AbsSender absSender) {
        var operationName = String.format("Удаление сообщения (msgId = %d) в чате (chatId = %d)", msg.getMessageId(), chat.getId());

        try {
            var deleteMsg = DeleteMessage.builder()
                    .chatId(chat.getId())
                    .messageId(msg.getMessageId())
                    .build();
            absSender.execute(deleteMsg);
            return msg;
        } catch (TelegramApiException ex) {
            throw new BotException(operationName, ex);
        }
    }
}
