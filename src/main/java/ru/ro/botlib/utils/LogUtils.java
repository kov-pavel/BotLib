package ru.ro.botlib.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class LogUtils {


    public static String parseObjectForLog(Object obj) {
        try {
            log.info("Преобразую объект к красивому виду...");
            return SDKUtils.OBJECT_WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            log.info("Операция по представлению объекта в красивом виде прервана. Причина: {}",
                    parseExceptionForLog(ex));
            throw new RuntimeException(ex);
        } finally {
            log.info("Объект преобразован успешно.");
        }
    }

    public static String parseExceptionForLog(Exception ex) {
        return ex.getLocalizedMessage() + "\n" + Arrays.toString(ex.getStackTrace());
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
