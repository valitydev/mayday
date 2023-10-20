package dev.vality.alerting.mayday.alertmanager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "k8s.alertmanager-configuration")
@Getter
@Setter
public class K8sAlertmanagerProperties {
    private Map<String, String> labels;
}
