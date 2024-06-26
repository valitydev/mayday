package dev.vality.alerting.mayday.alerttemplate.service;

import dev.vality.alerting.mayday.alerttemplate.dao.DawayDao;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.DictionaryType;
import dev.vality.alerting.mayday.alerttemplate.model.daway.*;
import dev.vality.alerting.mayday.alerttemplate.model.dictionary.DictionaryData;
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

    public Map<String, DictionaryData> getDictionary(DictionaryType type) {
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
            case PAYMENT_LIMIT_SCOPES -> Map.of(
                    "Провайдер", new DictionaryData("provider"),
                    "Провайдер + терминал", new DictionaryData("provider,terminal"),
                    "Провайдер + терминал + магазин", new DictionaryData("provider,shop,terminal"),
                    "Терминал", new DictionaryData("terminal"),
                    "Магазин", new DictionaryData("shop")
            );
            case PAYOUT_LIMIT_SCOPES -> Map.of(
                    "Провайдер", new DictionaryData("provider"),
                    "Провайдер + терминал", new DictionaryData("provider,terminal"),
                    "Провайдер + терминал + кошелек", new DictionaryData("provider,terminal,wallet"),
                    "Терминал", new DictionaryData("terminal"),
                    "Кошелёк", new DictionaryData("wallet")
            );
            case CONDITIONAL_BOUNDARIES -> Map.of(
                    "Больше порогового значения", new DictionaryData(">"),
                    "Меньше порогового значения", new DictionaryData("<")
            );
            case TIME_INTERVAL_BOUNDARIES -> Map.of(
                    "Да", new DictionaryData("unless"),
                    "Нет", new DictionaryData("and")
            );
            case AGGREGATION_INTERVALS -> Map.of(
                    "5 минут", new DictionaryData("5m"),
                    "15 минут", new DictionaryData("15m"),
                    "30 минут", new DictionaryData("30m"),
                    "1 час", new DictionaryData("1h"),
                    "3 часа", new DictionaryData("3h"),
                    "6 часов", new DictionaryData("6h"),
                    "12 часов", new DictionaryData("12h"),
                    "24 часа", new DictionaryData("24h"),
                    "Текущий календарный день (MSK)", new DictionaryData("today_msk"));
        };
    }

    private Map<String, DictionaryData> convertTerminalsToDictionary(List<Terminal> terminals) {
        return terminals.stream()
                .map(terminal -> createDictionaryData(terminal.getId(), terminal.getName()))
                .collect(Collectors.toMap(DictionaryData::getUserFriendlyValue, dictionaryData -> dictionaryData));
    }

    private Map<String, DictionaryData> convertProvidersToDictionary(List<Provider> providers) {
        return providers.stream()
                .map(provider -> createDictionaryData(provider.getId(), provider.getName()))
                .collect(Collectors.toMap(DictionaryData::getUserFriendlyValue, dictionaryData -> dictionaryData));
    }

    private Map<String, DictionaryData> convertWalletsToDictionary(List<Wallet> wallets) {
        return wallets.stream()
                .map(wallet -> createDictionaryData(wallet.getId(), wallet.getName()))
                .collect(Collectors.toMap(DictionaryData::getUserFriendlyValue, dictionaryData -> dictionaryData));
    }

    private Map<String, DictionaryData> convertShopsToDictionary(List<Shop> shops) {
        return shops.stream().collect(Collectors.toMap(
                shop -> formatDictionaryString(formatShopId(shop.getId()), shop.getName()),
                shop -> createDictionaryData(shop.getId(), shop.getName())
        ));
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

    private Map<String, DictionaryData> convertCurrenciesToDictionary(List<Currency> currencies) {
        return currencies.stream()
                .map(currency -> createDictionaryData(currency.getSymbolicCode(), currency.getName()))
                .collect(Collectors.toMap(DictionaryData::getUserFriendlyValue, dictionaryData -> dictionaryData));
    }

    private String formatDictionaryString(String id, String description) {
        return String.format("(%s) %s", id, description);
    }

    private DictionaryData createDictionaryData(String id, String description) {
        return DictionaryData.builder()
                .value(id)
                .userFriendlyValue(formatDictionaryString(id, description))
                .build();
    }

    private DictionaryData createDictionaryData(Integer id, String description) {
        return createDictionaryData(Integer.toString(id), description);
    }

}
