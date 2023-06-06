package dev.vality.alerting.mayday.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class K8sConfig {

    @Bean
    public Config k8sConfig() {
        return new ConfigBuilder().withDefaultNamespace().build();
    }
}
