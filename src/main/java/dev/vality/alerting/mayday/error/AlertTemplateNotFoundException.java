package dev.vality.alerting.mayday.error;

public class AlertTemplateNotFoundException extends RuntimeException {

    public AlertTemplateNotFoundException(String message) {
        super(message);
    }

}
