package dev.vality.alerting.mayday.client.model.alertmanager;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
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
        private Set<ChildRoute> routes = new HashSet<>();
    }

    @Data
    public static class ChildRoute {
        private String receiver;
        private Set<String> groupBy;
        private Set<Matcher> matchers;
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

    @Data
    public static class Matcher {
        private String name;
        private String value;
        private String matchType;
        private boolean regex;
    }

}
