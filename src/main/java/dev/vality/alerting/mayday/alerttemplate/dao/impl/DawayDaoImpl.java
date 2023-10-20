package dev.vality.alerting.mayday.alerttemplate.dao.impl;

import dev.vality.alerting.mayday.alerttemplate.dao.DawayDao;
import dev.vality.alerting.mayday.alerttemplate.model.daway.*;
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
    public List<Terminal> getAllTerminals() {
        return jdbcTemplate
                .query("select terminal_ref_id, name from dw.terminal where current = true", terminalRowMapper);
    }

    @Override
    public List<Terminal> getPaymentTerminals() {
        return jdbcTemplate
                .query("select t.terminal_ref_id, t.name from dw.terminal as t inner join dw.provider as p on t" +
                        ".terminal_provider_ref_id = p.provider_ref_id and p.current and p.payment_terms_json is not " +
                        "null where t.current", terminalRowMapper);
    }

    @Override
    public List<Terminal> getPayoutTerminals() {
        return jdbcTemplate
                .query("select t.terminal_ref_id, t.name from dw.terminal as t inner join dw.provider as p on t" +
                        ".terminal_provider_ref_id = p.provider_ref_id and p.current and p.wallet_terms_json is not " +
                        "null where t.current", terminalRowMapper);
    }

    @Override
    public List<Provider> getAllProviders() {
        return jdbcTemplate
                .query("select provider_ref_id, name from dw.provider where current = true", providerRowMapper);
    }

    @Override
    public List<Provider> getPaymentProviders() {
        return jdbcTemplate
                .query("select provider_ref_id, name from dw.provider where current = true and payment_terms_json is " +
                        "not null", providerRowMapper);
    }

    @Override
    public List<Provider> getPayoutProviders() {
        return jdbcTemplate
                .query("select provider_ref_id, name from dw.provider where current = true and wallet_terms_json is " +
                        "not null", providerRowMapper);
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
