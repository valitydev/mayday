package dev.vality.alerting.mayday.client.model.alertmanager;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Data
@RequiredArgsConstructor
public class AlertmanagerConfigSpec {

    private Route route;


    @Data
    public static class Route {
        private Set<String> groupBy;
        private String groupWait;
        private String groupInterval;
        private String repeatInterval;
        private Set<Receiver> routes;
    }

    @Data
    public static class Receiver {
        private String receiver;
        private Set<String> groupBy;
        private Set<String> matchers;
        private String groupWait;
        private String groupInterval;
        private String repeatInterval;
    }

}
