package dev.vality.alerting.mayday.alerttemplate.dao.impl.mapper;

import dev.vality.alerting.mayday.alerttemplate.model.daway.Shop;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ShopRowMapper implements RowMapper<Shop> {
    @Override
    public Shop mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Shop.builder()
                .id(rs.getString("shop_id"))
                .name(rs.getString("details_name"))
                .build();
    }
}
