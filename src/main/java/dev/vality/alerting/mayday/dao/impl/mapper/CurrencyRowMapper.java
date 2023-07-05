package dev.vality.alerting.mayday.dao.impl.mapper;

import dev.vality.alerting.mayday.model.daway.Currency;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class CurrencyRowMapper implements RowMapper<Currency> {
    @Override
    public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Currency.builder()
                .symbolicCode(rs.getString("symbolic_code"))
                .name(rs.getString("name"))
                .build();
    }
}
