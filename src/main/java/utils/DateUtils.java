package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class DateUtils {

    public static final String MMMM_D_YYYY = "MMMM d yyyy";    // November 3 2026
    public static final String DD_MM_YYYY = "dd/MM/yyyy";      // 03/11/2026
    public static final String YYYY_MM_DD = "yyyy-MM-dd";      // 2026-11-03
    public static final String YYYYMMDD_HHMMSS = "yyyyMMdd_HHmmss";   // 20261103_142530
    public static final String HHMMSS = "HH:mm:ss";

    public static LocalTime getNow() {
        return LocalTime.parse(DateUtils.currentTimestamp(DateUtils.HHMMSS));
    }

    public static String currentTimestamp(String pattern) {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    public static String dateInDays(int days, String pattern) {
        return LocalDate.now()
                .plusDays(days)
                .format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
    }

    public static String formatDate(String date, String fromPattern, String toPattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(fromPattern, Locale.ENGLISH))
                .format(DateTimeFormatter.ofPattern(toPattern, Locale.ENGLISH));
    }
}