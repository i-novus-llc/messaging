package ru.inovus.messaging.n2o.utils;

import java.util.Calendar;
import java.util.Date;

//todo удалить этот костыль
public class LocalDateTimeUtils {
    public static String parseDateToMsk(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR_OF_DAY, 3);
        return new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(cal.getTime());
    }
}
