package dev.vality.alerting.mayday.client.error;

public class PrometheusConfigurationException extends RuntimeException {

    public PrometheusConfigurationException(String message) {
        super(message);
    }
}
