package kov.pavel.botlib.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TimeUtils {

    public static Date now() {
        return new Date(System.currentTimeMillis());
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
}
