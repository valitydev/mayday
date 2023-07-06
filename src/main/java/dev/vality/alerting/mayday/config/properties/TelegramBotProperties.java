package dev.vality.alerting.mayday.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Configuration
@ConfigurationProperties(prefix = "telegram-bot")
@Validated
@Getter
@Setter
public class TelegramBotProperties {

    @NotNull
    private Resource url;
    private int networkTimeout = 5000;
}
