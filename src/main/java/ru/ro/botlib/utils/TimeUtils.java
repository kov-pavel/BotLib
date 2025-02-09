package ru.ro.botlib.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeUtils {

    public static Date now() {
        return new Date(System.currentTimeMillis());
    }

    public static Date nowPlusSeconds(int nSeconds) {
        var now = now();
        var calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.SECOND, nSeconds);
        return calendar.getTime();
    }

    public static Date atHours(int hours) {
        // Получаем текущий момент времени
        var calendar = Calendar.getInstance();
        var now = calendar.getTime();

        // Устанавливаем указанный час и обнуляем минуты, секунды, миллисекунды
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // Если полученное время уже прошло, добавляем 1 день
        if (calendar.getTime().before(now)) {
            calendar.add(Calendar.DATE, 1);
        }

        return calendar.getTime();
    }

    public static Date nowPlusDays(int nDays) {
        var now = now();
        var calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.DATE, nDays);
        return calendar.getTime();
    }

    public static long dateDiff(Date date1, Date date2, TimeUnit timeUnit) {
        return timeUnit.convert(
                Math.abs(date2.getTime() - date1.getTime()),
                TimeUnit.MILLISECONDS
        );
    }

    public static int date2unix(Date date) {
        return (int) (date.getTime() / 1000);
    }

    public static Date unix2date(int unixTime) {
        return new Date(unixTime * 1000L);
    }

    public static String formatOutputDate(Date date) {
        var sb = new StringBuilder();

        var dateStr = date.toString();
        var dateParts = dateStr.split(" ");

        var YY_MM_DD = dateParts[0].split("-");
        var YY = YY_MM_DD[0];
        var MM = YY_MM_DD[1];
        var DD = YY_MM_DD[2];
        sb.append(DD).append("/").append(MM).append("/").append(YY);

        return sb.toString();
    }
}
