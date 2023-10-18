package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.alerttemplate.model.daway.Currency;
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
                        .id("def91399-75ff-4307-8634-626c85859ea4")
                        .name("test").build()
        );
    }

    public static List<Currency> getTestCurrencies() {
        return List.of(
                Currency.builder()
                        .symbolicCode("RUB")
                        .name("Рублик").build()
        );
    }
}
