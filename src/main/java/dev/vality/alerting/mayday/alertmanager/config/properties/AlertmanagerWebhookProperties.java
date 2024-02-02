package dev.vality.alerting.mayday.alertmanager.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "alertmanager.webhook")
@Validated
@Getter
@Setter
public class AlertmanagerWebhookProperties {
    @NotNull
    private String url;
    private String path;
    private Boolean sendResolved = true;
}
