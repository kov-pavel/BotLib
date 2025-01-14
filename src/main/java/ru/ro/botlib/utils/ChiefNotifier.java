package ru.ro.botlib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.chat.CustomChat;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class ChiefNotifier {
    
    private final CustomChat chat;

    public void notifyChief(Message msg) {
        notifyChief(
                msg.getFrom(),
                msg.getChat(),
                msg.getText(),
                Optional.empty(),
                Optional.empty()
        );
    }

    public void notifyChief(User user, Chat chat, String[] args, BotCommand command) {
        notifyChief(
                user,
                chat,
                Arrays.stream(args).toList().toString(),
                Optional.of(command),
                Optional.empty()
        );
    }

    public void notifyChief(
            User user,
            Chat chat,
            String args,
            Optional<BotCommand> commandO,
            Optional<Exception> exO
    ) {
        try {
            var cause = exO
                    .map(e -> "Exception handled!\n\n" + e.getMessage())
                    .orElse("Illegal access to bot!");
            var parsedUser = parseUserForLog(user);
            var parsedChat = parseChatForLog(chat);
            var textSb = new StringBuilder();
            textSb
                    .append("Cause: ").append(cause)
                    .append("\n\nFrom: ").append(parsedUser)
                    .append("\n\nIn: ").append(parsedChat)
                    .append("\n\nFor command: /").append(commandO.map(BotCommand::getCommandIdentifier).orElse("[ABSENT]"))
                    .append("\n\nWith args: ").append(StringUtils.quote(args));
            notifyChief(textSb.toString());
        } catch (JsonProcessingException ex) {
            log.info("Возникло исключение при нотификации шефа об ошибке.", ex);
            throw new RuntimeException(ex);
        }
    }

    public void notifyChief(Exception ex) {
        notifyChief(
                LogUtils.parseExceptionForLog(ex)
        );
    }

    public void notifyChief(Exception ex, Update update) {
        notifyChief(
                LogUtils.parseExceptionForLog(ex),
                update
        );
    }

    public void notifyChief(String errorText, Update update) {
        var textSb = new StringBuilder();

        textSb
                .append("При обработке обновления:\n").append(LogUtils.parseObjectForLog(update))
                .append("\n\nВозникла ошибка: ").append(errorText);

        notifyChief(
                textSb.toString()
        );
    }

    public void notifyChief(String text) {
        try {
            log.info("Формирование сообщения...");
            var sendMsg = SendMessage.builder()
                    .chatId(chat.getChatId())
                    .messageThreadId(chat.getThreadId())
                    .text(text)
                    .build();
            log.info("Сообщение сформировано.");

            log.info("Отправка сообщения шефу...");
            Utils.ABS_SENDER.execute(sendMsg);
            log.info("Сообщение отправлено шефу.");
        } catch (TelegramApiException ex) {
            log.error("Получено исключение при отправке сообщения шефу!", ex);
            throw new RuntimeException(ex);
        }
    }

    public String parseUserForLog(User user) throws JsonProcessingException {
        return Utils.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(user);
    }

    public String parseChatForLog(Chat chat) throws JsonProcessingException {
        return Utils.OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(chat);
    }
}
