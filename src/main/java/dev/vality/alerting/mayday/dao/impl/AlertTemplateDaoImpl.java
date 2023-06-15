package dev.vality.alerting.mayday.dao.impl;

import dev.vality.alerting.mayday.dao.AlertTemplateDao;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertTemplate;
import dev.vality.mapper.RecordRowMapper;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;
import java.util.UUID;

import static dev.vality.alerting.mayday.domain.tables.AlertTemplate.ALERT_TEMPLATE;

@Component
public class AlertTemplateDaoImpl extends AbstractDao implements AlertTemplateDao {

    private final RowMapper<AlertTemplate> listRecordRowMapper;

    public AlertTemplateDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(ALERT_TEMPLATE, AlertTemplate.class);
    }

    @Override
    public List<AlertTemplate> findAll() {
        Query query = getDslContext().selectFrom(ALERT_TEMPLATE);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public AlertTemplate findById(String metricTemplateId) {
        Query query = getDslContext().selectFrom(ALERT_TEMPLATE)
                .where(ALERT_TEMPLATE.ID.eq(UUID.fromString(metricTemplateId)));
        return fetchOne(query, listRecordRowMapper);
    }
}
