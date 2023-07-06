package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.model.daway.*;

import java.util.List;

public interface DawayDao {

    List<Terminal> getTerminals();

    List<Provider> getProviders();

    List<Shop> getShops();

    List<Wallet> getWallets();

    List<Currency> getCurrencies();
}
