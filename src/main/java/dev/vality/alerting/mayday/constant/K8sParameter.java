package dev.vality.alerting.mayday.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class K8sParameter {
    public static final String PROMETHEUS_RULE_NAME = "mayday-managed-rule";
    public static final String ALERTMANAGER_CONFIG_NAME = "mayday-managed-config";
    public static final String ALERTMANAGER_RECEIVER_NAME = "mayday";

}
