package ru.ro.botlib.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.utils.ChatUtils;
import ru.ro.botlib.utils.LogUtils;
import ru.ro.botlib.utils.SDKUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
public abstract class CustomBotCommand extends BotCommand {

    private static final Executor executor = Executors.newFixedThreadPool(5);

    public CustomBotCommand(String commandIdentifier, String description) {
        super(commandIdentifier, description);
        log.info("HELLO, WORLD!");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        executor.execute(() -> {
            try {
                LogUtils.logBlockSeparator(true);
                log.info("Вызов команды /{}, START", getCommandIdentifier());

                if (SDKUtils.IS_ADMIN_PREDICATE.test(user.getId())
                        && ChatUtils.isPrivateChat(chat)
                ) {
                    executeInner(SDKUtils.ABS_SENDER, user, chat, Arrays.stream(arguments).toList());
                } else {
                    SDKUtils.CHIEF_NOTIFIER.notifyChief(user, chat, arguments, this);
                }
            } catch (Exception ex) {
                SDKUtils.CHIEF_NOTIFIER.notifyChief(user, chat, arguments, this);
            } finally {
                log.info("Вызов команды /{}, END", getCommandIdentifier());
                LogUtils.logBlockSeparator(false);
            }
        });
    }

    protected void executeInner(AbsSender absSender, User user, Chat chat, List<String> arguments) throws Exception {
        throw new NotImplementedException();
    }
}
