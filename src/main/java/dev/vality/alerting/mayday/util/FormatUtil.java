package dev.vality.alerting.mayday.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FormatUtil {

    public static String formatDuration(String durationInMinutes) {
        return durationInMinutes + "m";
    }
}
