package dev.vality.alerting.mayday.prometheus.client.k8s.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
public class PrometheusRuleSpec {

    private List<Group> groups;

    @Data
    @NoArgsConstructor
    public static class Group {
        private String name;
        private String interval;
        private List<Rule> rules;
        @JsonProperty("partial_response_strategy")
        private String partialResponseStrategy;
        private Integer limit;
    }

    @Data
    @NoArgsConstructor
    public static class Rule {
        private String record;
        private String alert;
        private String expr;
        @JsonProperty("for")
        private String duration;
        @JsonProperty("keep_firing_for")
        private String keepFiringFor;
        private Map<String, String> labels;
        private Map<String, String> annotations;
    }
}
