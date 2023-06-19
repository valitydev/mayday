package dev.vality.alerting.mayday.constant;

import lombok.experimental.UtilityClass;

@UtilityClass

public class PrometheusRuleAnnotation {

    public static final String ALERT_NAME = "alertname";
    public static final String USERNAME = "username";
    public static final String ALERT_DESCRIPTION = "alert_description";
    public static final String ALERT_FIRING_PREFIX = "Активно: ";
    public static final String ALERT_NOT_FIRING_PREFIX = "Более не активно: ";
}
