package dev.vality.alerting.mayday.alerttemplate.dao.impl.mapper;

import dev.vality.alerting.mayday.alerttemplate.model.daway.Terminal;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class TerminalRowMapper implements RowMapper<Terminal> {
    @Override
    public Terminal mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Terminal.builder()
                .id(rs.getInt("terminal_ref_id"))
                .name(rs.getString("name"))
                .build();
    }
}
