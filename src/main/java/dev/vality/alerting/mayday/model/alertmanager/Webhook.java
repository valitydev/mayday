package dev.vality.alerting.mayday.model.alertmanager;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Webhook {

    private String status;
    private String receiver;
    private List<Alert> alerts;

    @Data
    public static class Alert {

        private String status;
        private Map<String, String> labels;
        private Map<String, String> annotations;

    }
}
