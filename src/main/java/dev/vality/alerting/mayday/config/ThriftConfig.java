package dev.vality.alerting.mayday.config;

import dev.vality.alerting.mayday.config.properties.TelegramBotProperties;
import dev.vality.alerting.tg_bot.NotifierServiceSrv;
import dev.vality.woody.thrift.impl.http.THSpawnClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class ThriftConfig {

    @Bean
    public NotifierServiceSrv.Iface telegramBotClient(TelegramBotProperties properties) throws IOException {
        return new THSpawnClientBuilder()
                .withAddress(properties.getUrl().getURI())
                .withNetworkTimeout(properties.getNetworkTimeout())
                .build(NotifierServiceSrv.Iface.class);
    }
}
