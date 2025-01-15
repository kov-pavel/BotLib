package ru.ro.botlib.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.utils.ChatUtils;
import ru.ro.botlib.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public abstract class CustomBotCommand extends BotCommand {

    private static final Executor executor = Executors.newFixedThreadPool(5);

    public CustomBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        executor.execute(() -> {
            try {
                if (Utils.IS_ADMIN_PREDICATE.test(user.getId())
                        && ChatUtils.isPrivateChat(chat)
                ) {
                    executeInner(Utils.ABS_SENDER, user, chat, Arrays.stream(arguments).toList());
                } else {
                    Utils.CHIEF_NOTIFIER.notifyChief(user, chat, arguments, this);
                }
            } catch (Exception ex) {
                Utils.CHIEF_NOTIFIER.notifyChief(user, chat, arguments, this);
            }
        });
    }

    protected void executeInner(AbsSender absSender, User user, Chat chat, List<String> arguments) throws Exception {
        throw new NotImplementedException();
    }
}
