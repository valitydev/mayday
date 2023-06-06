package dev.vality.alerting.mayday.client.model.prometheus;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Builder
@RequiredArgsConstructor
public class PrometheusRuleSpec {

    private Set<Group> groups;

    @Data
    @Builder
    public static class Group {
        private String name;
        private Set<Rule> rules;
    }

    @Data
    @Builder
    public static class Rule {
        private String alert;
        private String expr;
        @JsonProperty("for")
        private String duration;
        private Map<String, String> labels;
        private Map<String, String> annotations;
    }
}
