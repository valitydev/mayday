package dev.vality.alerting.mayday.model;

import lombok.Data;

import java.util.List;

@Data
public class AlertmanagerWebhook {

    private String status;
    private String receiver;
    private List<Alert> alerts;

    @Data
    public static class Alert {

        private String status;
        //TODO: check format
        private String labels;
        private String annotations;

    }
}
