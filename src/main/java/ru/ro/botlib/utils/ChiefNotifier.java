package ru.ro.botlib.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.ro.botlib.chat.CustomChat;
import ru.ro.botlib.utils.log.LogUtils;

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
                LogUtils.parseExceptionForLog(ex)
        );
    }

    public void notifyChief(Exception ex, String additionalText) {
        notifyChief(
                additionalText + "\n\n" + LogUtils.parseExceptionForLog(ex)
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
                .append("\n\nВозникла ошибка:\n").append(errorText);

        notifyChief(
                textSb.toString()
        );
    }

    public void notifyChief(String text) {
        ChatUtils.sendMessageAsync(chat.getChatId(), chat.getThreadId(), text, SDKUtils.ABS_SENDER);
    }
}
