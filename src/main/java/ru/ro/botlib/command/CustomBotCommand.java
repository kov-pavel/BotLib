package ru.ro.botlib.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.exception.BotException;
import ru.ro.botlib.utils.ChatUtils;
import ru.ro.botlib.utils.SDKUtils;
import ru.ro.botlib.utils.log.LogUtils;

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
            var operationName = String.format("Вызов команды - /%s - с аргументами - %s",
                    getCommandIdentifier(),
                    LogUtils.parseObjectForLog(arguments)
            );

            try {
                LogUtils.logBlockSeparator(true, operationName);

                log.info("Проверка на админа...");
                if (SDKUtils.IS_ADMIN_PREDICATE.test(user.getId())
                        && ChatUtils.isPrivateChat(chat)
                ) {
                    log.info("Команда отправлена Админом. Продолжаю обработку...");
                    executeInner(SDKUtils.ABS_SENDER, user, chat, Arrays.stream(arguments).toList());
                } else {
                    log.info("Команда отправлена НЕ Админом. Уведомляю об этом Шефа и прекращаю обработку...");
                    SDKUtils.CHIEF_NOTIFIER.notifyChief(user, chat, arguments, this);
                }
            } catch (Exception ex) {
                BotException.describeLogAndChiefAndClient(operationName, ex, chat.getId(), absSender);
            } finally {
                LogUtils.logBlockSeparator(false, operationName);
            }
        });
    }

    protected void executeInner(AbsSender absSender, User user, Chat chat, List<String> arguments) throws Exception {
        throw new NotImplementedException();
    }

    public String getTemplateArguments() {
        throw new NotImplementedException();
    }

    private boolean hasTemplateArguments() {
        return StringUtils.hasText(getTemplateArguments());
    }

    public List<String> getUseCasesArguments() {
        throw new NotImplementedException();
    }

    private boolean hasUseCasesArguments() {
        return !getUseCasesArguments().isEmpty();
    }

    public SendMessage getDescriptionSendMessage(long chatId) {
        var commandDescriptionSb = new StringBuilder();

        commandDescriptionSb
                .append("Описание: ").append(getDescription()).append("\n\n")
                .append("Исходник:\n\n```\n/").append(getCommandIdentifier()).append("\n```\n\n")
                .append("Шаблон:\n\n```\n/").append(getCommandIdentifier());

        if (hasTemplateArguments()) {
            commandDescriptionSb
                    .append(" ").append(getTemplateArguments());
        }

        commandDescriptionSb.append("```\n\n");

        if (hasUseCasesArguments()) {
            commandDescriptionSb
                    .append("Примеры использования:");

            getUseCasesArguments().forEach(useCaseArguments ->
                    commandDescriptionSb.append("\n\n```\n/").append(getCommandIdentifier()).append(" ").append(useCaseArguments).append("\n```")
            );
        }

        var sendMsd = SendMessage.builder()
                .chatId(chatId)
                .text(commandDescriptionSb.toString())
                .parseMode("MarkdownV2")
                .build();

        return sendMsd;
    }
}
