package kov.pavel.botlib.utils.admin;

import kov.pavel.botlib.utils.GeneralUtils;
import kov.pavel.botlib.utils.LogUtils;
import kov.pavel.botlib.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Date;
import java.util.List;

@Slf4j
public class AdminUtils {

    public static void deleteAllMessages(
            List<AdminDeleteMessageDto> messages,
            AbsSender absSender
    ) {
        messages.forEach(msg -> deleteMessage(msg.getChatID(), msg.getMessageID(), absSender));
    }

    public static void deleteMessage(long chatId, int msgId, AbsSender absSender) {
        try {
            log.info("Удаление сообщения из чата...");

            var deleteMessage = DeleteMessage.builder()
                    .chatId(chatId)
                    .messageId(msgId)
                    .build();
            absSender.execute(deleteMessage);

            log.info("Сообщение из чата удалено.");
        } catch (TelegramApiException ex) {
            var errorMsg = String.format("Ошибка при удалении сообщения (messageID = %s) из чата (chatID = %s)!", msgId, chatId);
            log.info(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
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
            log.info("Выполнение операции админа перед баном...");
            adminPreOperation.execute();
            log.info("Операция выполнена.\n");

            log.info("Формирования бана в Telegram API...");
            var bannedUntilUnix = TimeUtils.date2unix(untilDate);
            var banChatMember = BanChatMember.builder()
                    .chatId(chatId)
                    .userId(userId)
                    .untilDate(bannedUntilUnix)
                    .build();
            absSender.execute(banChatMember);
            log.info("Бан отправлен в Telegram API.\n");

            log.info("Удаление всех сообщений пользователя...");
            deleteAllMessages(messages, absSender);
            log.info("Все сообщения пользователя удалены.\n");
        } catch (Exception ex) {
            var errorMsg = String.format("Возникла ошибка при бане участника c tgId = `%s` в чате `%s`!\n", userId, chatId);
            log.info(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
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
            log.info("Формирование сообщения для отправки...");
            var sendMsg = SendMessage.builder()
                    .chatId(msg.getChatId())
                    .messageThreadId(msg.getMessageThreadId())
                    .text(msg.getText())
                    .parseMode("Markdown")
                    .build();
            log.info("Сообщение для отправки сформировано.");

            log.info("Отправка сообщения...");
            var sentMsg = absSender.execute(sendMsg);
            log.info("Сообщение отправлено.\n");

            return sentMsg;
        } catch (Exception ex) {
            var errorMsg = String.format("Возникла ошибка при отправке сообщения: %s\n", LogUtils.parseObjectForLog(msg));
            log.error(errorMsg, ex);
            throw new RuntimeException(errorMsg, ex);
        }
    }
}
