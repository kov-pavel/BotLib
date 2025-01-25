package ru.ro.botlib.exception;

public class NotFoundBotException extends BotException {

    public NotFoundBotException(String operationName, Throwable cause) {
        super(operationName, cause);
    }

    public NotFoundBotException(String operationName, String cause) {
        super(operationName, cause);
    }
}
