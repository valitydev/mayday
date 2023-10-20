package dev.vality.alerting.mayday.alertmanager.config;

import dev.vality.alerting.mayday.alertmanager.config.properties.TelegramBotReceiverProperties;
import dev.vality.alerting.tg_bot.NotifierServiceSrv;
import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TgBotReceiverConfig {

    @Bean
    public NotifierServiceSrv.Iface telegramBotClient(TelegramBotReceiverProperties properties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(properties.getUrl().getURI())
                .withNetworkTimeout(properties.getNetworkTimeout())
                .build(NotifierServiceSrv.Iface.class);
    }
}
