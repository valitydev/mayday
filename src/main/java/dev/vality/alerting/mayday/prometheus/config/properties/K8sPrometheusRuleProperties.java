package dev.vality.alerting.mayday.prometheus.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "k8s.prometheus-rule")
@Getter
@Setter
public class K8sPrometheusRuleProperties {
    private Map<String, String> labels;
    private AlertRule alertRule;


    @Getter
    @Setter
    public static class AlertRule {
        private Map<String, String> labels;
    }
}
