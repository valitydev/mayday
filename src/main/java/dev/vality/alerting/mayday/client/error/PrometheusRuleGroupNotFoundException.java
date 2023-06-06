package dev.vality.alerting.mayday.client.error;

public class PrometheusRuleGroupNotFoundException extends Exception {

    public PrometheusRuleGroupNotFoundException(String message) {
        super(message);
    }
}
