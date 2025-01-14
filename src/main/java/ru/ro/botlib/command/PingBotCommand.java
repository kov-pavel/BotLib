package ru.ro.botlib.command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.ro.botlib.utils.Utils;

import java.util.List;

@Component
@Slf4j
public class PingBotCommand extends CustomBotCommand {

    private static final String IDENTIFIER = "ping";
    private static final String DESCRIPTION = "Предоставляет данные о доступности бота";

    public PingBotCommand() {
        super(IDENTIFIER, DESCRIPTION);
    }

    @Override
    protected void executeInner(User user, Chat chat, List<String> arguments) throws Exception {
        executeOne(user, chat, arguments);
    }

    public void executeOne(User user, Chat chat, List<String> arguments)
            throws TelegramApiException {
        var msg = new SendMessage();
        msg.setChatId(chat.getId());
        msg.setText("pong");
        Utils.ABS_SENDER.execute(msg);
    }
}
