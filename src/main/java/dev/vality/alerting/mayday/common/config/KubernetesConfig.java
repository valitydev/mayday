package dev.vality.alerting.mayday.common.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KubernetesConfig {

    @Bean
    public Config k8sConfig() {
        return new ConfigBuilder().withDefaultNamespace().build();
    }

}
