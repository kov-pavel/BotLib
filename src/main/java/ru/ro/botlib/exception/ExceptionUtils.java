package ru.ro.botlib.exception;

import org.springframework.util.StringUtils;

public class ExceptionUtils {

    public static String makeOperationMessage(String operationName) {
        return "Возникла ошибка при выполнении операции: " + StringUtils.quote(operationName) + "!";
    }
}
