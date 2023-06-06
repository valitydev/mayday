package dev.vality.alerting.mayday.client.error;

public class PrometheusRuleNotFoundException extends Exception {

    public PrometheusRuleNotFoundException(String message) {
        super(message);
    }
}
