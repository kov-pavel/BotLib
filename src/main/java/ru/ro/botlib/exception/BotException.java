package ru.ro.botlib.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.ro.botlib.utils.ChatUtils;
import ru.ro.botlib.utils.SDKUtils;
import ru.ro.botlib.utils.log.LogUtils;

import java.util.Optional;

@Getter
@Slf4j
public class BotException extends RuntimeException {

    private final String logMessage;
    private final String clientMessage;

    public BotException(String operationName) {
        this(operationName, "");
    }

    public BotException(String operationName, Throwable cause) {
        this(operationName, cause.getMessage(), Optional.of(cause));
    }

    public BotException(String operationName, String clientMessage) {
        this(operationName, clientMessage, Optional.empty());
    }

    public BotException(String operationName, String clientMessage, Optional<Throwable> exO) {
        var logMessageSb = new StringBuilder()
                .append(ExceptionUtils.makeOperationMessage(operationName))
                .append(". Подробнее: ")
                .append(clientMessage);

        exO.ifPresent(e -> logMessageSb
                .append("\n\nStackTrace:\n")
                .append(LogUtils.parseExceptionForLog(e))
        );

        this.logMessage = logMessageSb.toString();

        this.clientMessage = clientMessage;
    }

    public String getClientMessage() {
        return hasClientMessage() ? this.clientMessage : "Неизвестная ошибка!";
    }

    private boolean hasClientMessage() {
        return StringUtils.hasText(this.clientMessage);
    }

    public static DescribeResponse describeLog(String operationName, Exception ex) {
        BotException botEx = ex instanceof BotException ? (BotException) ex : new BotException(operationName, ex);
        var logMsg = botEx.getLogMessage();
        log.error(logMsg);

        return DescribeResponse.builder()
                .botException(botEx)
                .logMessage(logMsg)
                .clientMessage(botEx.getClientMessage())
                .build();
    }

    public static DescribeResponse describeLogAndChief(String operationName, Exception ex) {
        var describeResponse = describeLog(operationName, ex);

        SDKUtils.CHIEF_NOTIFIER.notifyChief(
                ex,
                describeResponse.getLogMessage()
        );

        return describeResponse;
    }

    public static void describeLogAndChiefAndClient(String operationName, Exception ex, long chatId, AbsSender absSender) {
        var describeResponse = describeLogAndChief(operationName, ex);

        String clientMsg = describeResponse.getClientMessage();
        ChatUtils.sendMessageSync(chatId, clientMsg, absSender);
    }

    @AllArgsConstructor
    @Getter
    @Builder
    public static final class DescribeResponse {
        private final BotException botException;
        private final String logMessage;
        private final String clientMessage;
    }
}
