package dev.vality.alerting.mayday.client.model.alertmanager;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class AlertmanagerConfigSpec {

    private Route route;
    private Set<Receiver> receivers;


    @Data
    public static class Route {
        private String receiver;
        private Set<String> groupBy;
        private String groupWait;
        private String groupInterval;
        private String repeatInterval;
        private Set<ChildRoute> routes;
    }

    @Data
    public static class ChildRoute {
        private String receiver;
        private Set<String> groupBy;
        private Set<String> matchers;
        private String groupWait;
        private String groupInterval;
        private String repeatInterval;
    }

    @Data
    public static class Receiver {
        private String name;
        private Set<WebhookConfig> webhookConfigs;
    }

    @Data
    public static class WebhookConfig {
        private String url;
    }

}
