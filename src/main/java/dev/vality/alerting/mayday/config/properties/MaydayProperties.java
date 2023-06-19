package dev.vality.alerting.mayday.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "mayday")
@Validated
@Getter
@Setter
public class MaydayProperties {

    @NotNull
    private String webhookUrl;
    private String alertmanagerWebhookPath;
}
