package dev.vality.alerting.mayday.client.model.prometheus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class PrometheusRuleSpec {

    private Set<Group> groups = new HashSet<>();

    @Data
    @NoArgsConstructor
    public static class Group {
        private String name;
        private Set<Rule> rules;
    }

    @Data
    @NoArgsConstructor
    public static class Rule {
        private String alert;
        private String expr;
        @JsonProperty("for")
        private String duration;
        private Map<String, String> labels;
        private Map<String, String> annotations;
    }
}
