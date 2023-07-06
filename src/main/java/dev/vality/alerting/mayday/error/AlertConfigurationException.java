package dev.vality.alerting.mayday.error;

public class AlertConfigurationException extends RuntimeException {

    public AlertConfigurationException(String message) {
        super(message);
    }

    public AlertConfigurationException(String message, Throwable e) {
        super(message, e);
    }
}
