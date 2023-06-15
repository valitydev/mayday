package dev.vality.alerting.mayday.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "k8s")
@Getter
@Setter
public class KubernetesProperties {

    private String namespace;
}
