package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.alerttemplate.model.daway.Provider;
import dev.vality.alerting.mayday.alerttemplate.model.daway.Shop;
import dev.vality.alerting.mayday.alerttemplate.model.daway.Terminal;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class DawayObjectUtil {

    public static List<Provider> getTestProviders() {
        return List.of(
                Provider.builder()
                        .id(1)
                        .name("test").build()
        );
    }

    public static List<Terminal> getTestTerminals() {
        return List.of(
                Terminal.builder()
                        .id(1)
                        .name("test").build()
        );
    }

    public static List<Shop> getTestShops() {
        return List.of(
                Shop.builder()
                        .id("test")
                        .name("test").build()
        );
    }
}
