package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.model.daway.Provider;
import dev.vality.alerting.mayday.model.daway.Shop;
import dev.vality.alerting.mayday.model.daway.Terminal;
import dev.vality.alerting.mayday.model.daway.Wallet;

import java.util.List;

public interface DawayDao {

    List<Terminal> getTerminals();

    List<Provider> getProviders();

    List<Shop> getShops();

    List<Wallet> getWallets();
}
