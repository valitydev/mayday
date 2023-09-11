package dev.vality.alerting.mayday.alerttemplate.dao;

import dev.vality.alerting.mayday.alerttemplate.model.daway.*;

import java.util.List;

public interface DawayDao {

    List<Terminal> getAllTerminals();

    List<Terminal> getPaymentTerminals();

    List<Terminal> getPayoutTerminals();

    List<Provider> getAllProviders();

    List<Provider> getPaymentProviders();

    List<Provider> getPayoutProviders();

    List<Shop> getShops();

    List<Wallet> getWallets();

    List<Currency> getCurrencies();
}
