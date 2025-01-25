package ru.ro.botlib.utils.admin;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.utils.log.LogUtils;
import ru.ro.botlib.utils.TimeUtils;

import java.util.Date;
import java.util.List;

@Slf4j
public class AdminUtils {

    public static void deleteAllMessages(
            List<AdminDeleteMessageDto> messages,
            AbsSender absSender
    ) {
        messages.forEach(msg -> deleteMessage(msg.chatID(), msg.messageID(), absSender));
    }

    public static void deleteMessage(long chatId, int msgId, AbsSender absSender) {
        try {
            log.info("Удаление сообщения (msgID = {}) из чата (chatID = {}), START", msgId, chatId);

            var deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(msgId)
                    .build();
            absSender.execute(deleteMessage);
        } catch (TelegramApiException ex) {
            var errorMsg = String.format("Ошибка при удалении сообщения (messageID = %s) из чата (chatID = %s)!", msgId, chatId);
            throw new RuntimeException(errorMsg, ex);
        } finally {
            log.info("Удаление сообщения из чата, END");
        }
    }

    public static void banParticipant(
            AdminPreOperation adminPreOperation,
            Date untilDate,
            AbsSender absSender,
            long chatId,
            long userId,
            List<AdminDeleteMessageDto> messages
    ) {
        try {
            log.info("\nПроцедура бана участника, START");

            log.info("Выполнение операции админа перед баном, START");
            adminPreOperation.execute();
            log.info("Выполнение операции админа перед баном, END");

            log.info("Формирование и отправка бана в Telegram API, START");
            var bannedUntilUnix = TimeUtils.date2unix(untilDate);
            var banChatMember = BanChatMember.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .untilDate(bannedUntilUnix)
                    .build();
            absSender.execute(banChatMember);
            log.info("Формирование и отправка бана в Telegram API, END");

            log.info("Удаление всех сообщений пользователя, START");
            deleteAllMessages(messages, absSender);
            log.info("Удаление всех сообщений пользователя, END");
        } catch (Exception ex) {
            var errorMsg = String.format("Возникла ошибка при бане участника c tgId = `%s` в чате `%s`!\n", userId, chatId);
            log.info(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        } finally {
            log.info("Процедура бана участника, END");
        }
    }

    public static Message createMessage(long chatId, int msgThreadId, String text) {
        var msg = new Message();

        var chat = new Chat();
        chat.setId(chatId);
        msg.setChat(chat);

        msg.setMessageThreadId(msgThreadId);
        msg.setText(text);

        return msg;
    }

    public static Message sendMessage(Message msg, AbsSender absSender) {
        try {
            log.info("Формирование сообщения для отправки, START");
            var sendMsg = SendMessage.builder()
                    .chatId(msg.getChatId())
                    .messageThreadId(msg.getMessageThreadId())
                    .text(msg.getText())
                    .parseMode("Markdown")
                    .build();
            log.info("Формирование сообщения для отправки, END");

            log.info("Отправка сообщения, START");

            return absSender.execute(sendMsg);
        } catch (Exception ex) {
            var errorMsg = String.format("Возникла ошибка при отправке сообщения: %s\n", LogUtils.parseObjectForLog(msg));
            log.error(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        } finally {
            log.info("Отправка сообщения, END");
        }
    }
}
