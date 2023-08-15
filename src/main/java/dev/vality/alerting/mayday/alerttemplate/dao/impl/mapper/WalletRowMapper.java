package dev.vality.alerting.mayday.alerttemplate.dao.impl.mapper;

import dev.vality.alerting.mayday.alerttemplate.model.daway.Wallet;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class WalletRowMapper implements RowMapper<Wallet> {
    @Override
    public Wallet mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Wallet.builder()
                .id(rs.getString("wallet_id"))
                .name(rs.getString("wallet_name"))
                .build();
    }
}
