package ru.ro.botlib.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ro.botlib.chat.CustomChat;
import ru.ro.botlib.exception.BotException;

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
        var cause = exO
                .map(ex -> "Получено исключение! Подробнее: " + LogUtils.parseObjectForLog(ex))
                .orElse("Неразрешенный доступ к боту!");
        var parsedUser = LogUtils.parseObjectForLog(user);
        var parsedChat = LogUtils.parseObjectForLog(chat);
        var textSb = new StringBuilder();
        textSb
                .append("Причина: ").append(cause)
                .append("\n\nОт пользователя: ").append(parsedUser)
                .append("\n\nВ чате: ").append(parsedChat)
                .append("\n\nДля команды: /").append(commandO.map(BotCommand::getCommandIdentifier).orElse("[ABSENT]"))
                .append("\n\nС аргументами: ").append(StringUtils.quote(args));
        notifyChief(textSb.toString());
    }

    public void notifyChief(Exception ex) {
        notifyChief(
                LogUtils.parseObjectForLog(ex)
        );
    }

    public void notifyChief(Exception ex, String additionalText) {
        notifyChief(
                additionalText + "\n\n" + LogUtils.parseObjectForLog(ex)
        );
    }

    public void notifyChief(Exception ex, Update update) {
        notifyChief(
                LogUtils.parseObjectForLog(ex),
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
            log.info("Формирование нотификации Шефу, START");

            var sendMsg = SendMessage.builder()
                    .chatId(chat.getChatId())
                    .messageThreadId(chat.getThreadId())
                    .text(text)
                    .build();

            SDKUtils.ABS_SENDER.execute(sendMsg);
        } catch (Exception ex) {
            var errorMsg = "Получено исключение при отправке нотификации Шефу!";
            throw new BotException(errorMsg, ex);
        } finally {
            log.info("Формирование нотификации Шефу, END");
        }
    }
}
