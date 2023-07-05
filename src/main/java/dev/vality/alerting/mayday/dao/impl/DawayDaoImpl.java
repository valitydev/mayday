package dev.vality.alerting.mayday.dao.impl;

import dev.vality.alerting.mayday.dao.DawayDao;
import dev.vality.alerting.mayday.model.daway.*;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DawayDaoImpl implements DawayDao {

    private final RowMapper<Provider> providerRowMapper;
    private final RowMapper<Terminal> terminalRowMapper;
    private final RowMapper<Shop> shopRowMapper;
    private final RowMapper<Wallet> walletRowMapper;
    private final RowMapper<Currency> currencyRowMapper;
    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Terminal> getTerminals() {
        return jdbcTemplate
                .query("select terminal_ref_id, name from dw.terminal where current = true", terminalRowMapper);
    }

    @Override
    public List<Provider> getProviders() {
        return jdbcTemplate
                .query("select provider_ref_id, name from dw.provider where current = true", providerRowMapper);
    }

    @Override
    public List<Shop> getShops() {
        return jdbcTemplate
                .query("select distinct (shop_id), details_name from dw.shop where current = true", shopRowMapper);
    }

    @Override
    public List<Wallet> getWallets() {
        return jdbcTemplate
                .query("select wallet_id, wallet_name from dw.wallet where current = true", walletRowMapper);
    }

    @Override
    public List<Currency> getCurrencies() {
        return jdbcTemplate
                .query("select symbolic_code, name from dw.currency where current = true", currencyRowMapper);
    }
}
