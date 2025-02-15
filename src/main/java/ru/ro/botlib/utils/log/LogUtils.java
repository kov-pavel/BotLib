package ru.ro.botlib.utils.log;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import ru.ro.botlib.exception.BotException;
import ru.ro.botlib.utils.SDKUtils;
import ru.ro.botlib.utils.TimeUtils;

import java.io.PrintWriter;
import java.io.StringWriter;

@Slf4j
public class LogUtils {

    public static String parseObjectForLog(Object obj) {
        var operationName = "Преобразование объекта к красивому виду";
        try {
            return SDKUtils.OBJECT_WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException ex) {
            BotException.describeLog(operationName, ex);
        }

        return "";
    }

    public static String parseExceptionForLog(Throwable ex) {
        var operationName = "Преобразование исключения к красивому виду";
        try {
            var sw = new StringWriter();
            var pw = new PrintWriter(sw);

            // Записываем основную информацию об исключении
            pw.println("Exception Type: " + ex.getClass().getName());
            pw.println("Message: " + (ex.getMessage() != null ? ex.getMessage() : "N/A"));

            // Записываем стектрейс
            pw.println("\nStack Trace:");
            ex.printStackTrace(pw);

            return sw.toString();
        } catch (Exception ex2) {
            return "Не удалось выполнить операцию: " + StringUtils.quote(operationName);
        }
    }

    public static void logBlockSeparator(boolean isFirstLine, String operationName) {
        var logTextSb = new StringBuilder();

        if (isFirstLine) {
            logTextSb
                    .append("\n")
                    .append("========================================================").append(" ").append(TimeUtils.now())
                    .append("\n")
                    .append("*").append(operationName).append("*, [START]\n");
        } else {
            logTextSb
                    .append("\n")
                    .append("*").append(operationName).append("*, [END]")
                    .append("\n")
                    .append("========================================================");
        }

        log.info(logTextSb.toString());
    }

    public static void logSimpleBlockSeparator(boolean isFirstLine, String operationName) {
        var logTextSb = new StringBuilder();

        if (isFirstLine) {
            logTextSb
                    .append("\n*").append(operationName).append("*, [START]");
        } else {
            logTextSb
                    .append("*").append(operationName).append("*, [END]\n");
        }

        log.info(logTextSb.toString());
    }
}
