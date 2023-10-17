package dev.vality.alerting.mayday.alerttemplate.service;

import dev.vality.alerting.mayday.alerttemplate.dao.DawayDao;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.DictionaryType;
import dev.vality.alerting.mayday.alerttemplate.model.daway.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DawayDao dawayDao;

    public Map<String, String> getDictionary(DictionaryType type) {
        return switch (type) {
            case TERMINALS -> convertTerminalsToDictionary(dawayDao.getAllTerminals());
            case PAYMENT_TERMINALS -> convertTerminalsToDictionary(dawayDao.getPaymentTerminals());
            case PAYOUT_TERMINALS -> convertTerminalsToDictionary(dawayDao.getPayoutTerminals());
            case PROVIDERS -> convertProvidersToDictionary(dawayDao.getAllProviders());
            case PAYMENT_PROVIDERS -> convertProvidersToDictionary(dawayDao.getPaymentProviders());
            case PAYOUT_PROVIDERS -> convertProvidersToDictionary(dawayDao.getPayoutProviders());
            case WALLETS -> convertWalletsToDictionary(dawayDao.getWallets());
            case SHOPS -> convertShopsToDictionary(dawayDao.getShops());
            case CURRENCIES -> convertCurrenciesToDictionary(dawayDao.getCurrencies());
            case PAYMENT_LIMIT_SCOPES -> Map.of("Провайдер", "provider",
                    "Провайдер + терминал", "provider,terminal",
                    "Провайдер + терминал + магазин", "provider,shop,terminal",
                    "Терминал", "terminal",
                    "Магазин", "shop");
            case PAYOUT_LIMIT_SCOPES -> Map.of("Провайдер", "provider",
                    "Провайдер + терминал", "provider,terminal",
                    "Провайдер + терминал + кошелек", "provider,terminal,wallet",
                    "Терминал", "terminal",
                    "Кошелёк", "wallet");
            case CONDITIONAL_BOUNDARIES -> Map.of("Больше порогового значения", ">", "Меньше порогового значения", "<");
            case TIME_INTERVAL_BOUNDARIES -> Map.of("Да", "unless", "Нет", "and");
            case AGGREGATION_INTERVALS -> Map.of("5 минут", "5m", "15 минут", "15m", "30 минут", "30m",
                    "1 час", "1h", "3 часа", "3h", "6 часов", "6h", "12 часов", "12h", "24 часа", "24h");
        };
    }

    private Map<String, String> convertTerminalsToDictionary(List<Terminal> terminals) {
        return terminals.stream()
                .collect(Collectors.toMap(
                        terminal -> formatDictionaryKey(Integer.toString(terminal.getId()), terminal.getName()),
                        terminal -> Integer.toString(terminal.getId())));
    }

    private Map<String, String> convertProvidersToDictionary(List<Provider> providers) {
        return providers.stream()
                .collect(Collectors.toMap(
                        provider -> formatDictionaryKey(Integer.toString(provider.getId()), provider.getName()),
                        provider -> Integer.toString(provider.getId())));
    }

    private Map<String, String> convertWalletsToDictionary(List<Wallet> wallets) {
        return wallets.stream()
                .collect(Collectors.toMap(
                        wallet -> formatDictionaryKey(wallet.getId(), wallet.getName()),
                        Wallet::getId));
    }

    private Map<String, String> convertShopsToDictionary(List<Shop> shops) {
        return shops.stream()
                .collect(Collectors.toMap(
                        shop -> formatDictionaryKey(formatShopId(shop.getId()), shop.getName()),
                        Shop::getId));
    }

    // Возвращаем только часть UUID, т.к иначе строка выходит слишком длинной
    private String formatShopId(String shopId) {
        try {
            UUID.fromString(shopId);
            return shopId.substring(0, shopId.indexOf("-"));
        } catch (IllegalArgumentException e) {
            log.warn("Unable to format shopId '{}'", shopId);
            return shopId;
        }
    }

    private Map<String, String> convertCurrenciesToDictionary(List<Currency> currencies) {
        return currencies.stream()
                .collect(Collectors.toMap(
                        currency -> formatDictionaryKey(currency.getSymbolicCode(), currency.getName()),
                        Currency::getSymbolicCode));
    }

    private String formatDictionaryKey(String id, String description) {
        return String.format("(%s) %s", id, description);
    }

}
