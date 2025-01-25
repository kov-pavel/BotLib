package ru.ro.botlib.exception;

import org.springframework.util.StringUtils;

public class BotException extends RuntimeException {

    public BotException(String operationName) {
        super(makeMessageWithOperation(operationName));
    }

    public BotException(String operationName, Throwable cause) {
        super(makeMessageWithOperation(operationName), cause);
    }

    private static String makeMessageWithOperation(String operationName) {
        return "Возникла ошибка при выполнении операции: " + StringUtils.quote(operationName) + "!";
    }
}
