package dev.vality.alerting.mayday.alerttemplate.error;

public class AlertTemplateNotFoundException extends RuntimeException {

    public AlertTemplateNotFoundException(String message) {
        super(message);
    }

}
