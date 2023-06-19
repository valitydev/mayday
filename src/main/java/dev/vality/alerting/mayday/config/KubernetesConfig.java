package dev.vality.alerting.mayday.config;

import dev.vality.alerting.mayday.config.properties.KubernetesProperties;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class KubernetesConfig {

    private final KubernetesProperties kubernetesProperties;

    @Bean
    public Config k8sConfig() {
        if (ObjectUtils.isEmpty(kubernetesProperties.getNamespace())) {
            return new ConfigBuilder().withDefaultNamespace().build();
        }

        return new ConfigBuilder().withNamespace(kubernetesProperties.getNamespace()).build();
    }

}
