package dev.vality.alerting.mayday.util;

import lombok.experimental.UtilityClass;

import java.util.concurrent.TimeUnit;

@UtilityClass
public class FormatUtil {

    public static String formatMinutesDuration(String value) {
        return formatDuration(value, TimeUnit.MINUTES);
    }

    public static String formatSecondsDuration(String value) {
        return formatDuration(value, TimeUnit.SECONDS);
    }

    public static String formatDuration(String value, TimeUnit timeUnit) {
        String unit =  switch (timeUnit) {
            case MILLISECONDS -> "ms";
            case SECONDS -> "s";
            case MINUTES -> "m";
            case HOURS -> "h";
            case DAYS -> "d";
            default -> throw new IllegalArgumentException(timeUnit + " is not supported!");
        };
        return value + unit;
    }
}
