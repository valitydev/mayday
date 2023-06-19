package dev.vality.alerting.mayday.config.properties;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "k8s")
@Getter
@Setter
public class KubernetesProperties {

    private String namespace;

    private PrometheusRuleConfig prometheusRule;
    private AlertmanagerConfigurationConfig alertmanagerConfiguration;

    @Data
    public static class PrometheusRuleConfig {
        private Map<String, String> labels;
    }

    @Data
    public static class AlertmanagerConfigurationConfig {
        private Map<String, String> labels;
    }
}
