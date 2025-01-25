package ru.ro.botlib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import ru.ro.botlib.exception.BotException;

@Slf4j
public class LogUtils {

    public static String parseObjectForLog(Object obj) {
        var operationName = "Преобразование объекта к красивому виду";
        try {
            log.info("{}, START", operationName);
            return SDKUtils.OBJECT_WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            throw new BotException(operationName, ex);
        } finally {
            log.info("{}, END", operationName);
        }
    }

    public static void logBlockSeparator(boolean isFirstLine) {
        var logTextSb = new StringBuilder();

        if (isFirstLine) {
            logTextSb.append("\n");
        }

        logTextSb.append("========================================================");

        if (isFirstLine) {
            logTextSb.append(" ").append(TimeUtils.now());
        } else {
            logTextSb.append("\n");
        }

        log.info(logTextSb.toString());
    }
}
