package dev.vality.alerting.mayday.common.config;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

@Configuration
@RequiredArgsConstructor
public class KubernetesConfig {

    @Value("${k8s.namespace}")
    private String k8sNamespace;

    @Bean
    public Config k8sConfig() {
        if (ObjectUtils.isEmpty(k8sNamespace)) {
            return new ConfigBuilder().withDefaultNamespace().build();
        }

        return new ConfigBuilder().withNamespace(k8sNamespace).build();
    }

}
