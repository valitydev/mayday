package dev.vality.alerting.mayday.dao.impl.mapper;

import dev.vality.alerting.mayday.model.daway.Provider;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ProviderRowMapper implements RowMapper<Provider> {
    @Override
    public Provider mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Provider.builder()
                .id(rs.getInt("provider_ref_id"))
                .name(rs.getString("name"))
                .build();
    }
}
