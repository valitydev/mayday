package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.constant.DictionaryType;
import dev.vality.alerting.mayday.dao.DawayDao;
import dev.vality.alerting.mayday.model.daway.Provider;
import dev.vality.alerting.mayday.model.daway.Shop;
import dev.vality.alerting.mayday.model.daway.Terminal;
import dev.vality.alerting.mayday.model.daway.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryService {

    private final DawayDao dawayDao;

    public Map<String, String> getDictionary(DictionaryType type) {
        return switch (type) {
            case TERMINALS -> convertTerminalsToDictionary(dawayDao.getTerminals());
            case PROVIDERS -> convertProvidersToDictionary(dawayDao.getProviders());
            case WALLETS -> convertWalletsToDictionary(dawayDao.getWallets());
            case SHOPS -> convertShopsToDictionary(dawayDao.getShops());
            case BOUNDARIES -> Map.of("Больше порогового значения", ">", "Меньше порогового значения", "<");
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
                        shop -> formatDictionaryKey(shop.getId(), shop.getName()),
                        Shop::getId));
    }

    private String formatDictionaryKey(String id, String description) {
        return String.format("(%s) %s", id, description);
    }

}
