package dev.vality.alerting.mayday.dao.impl;

import dev.vality.alerting.mayday.dao.MetricTemplateDao;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;
import dev.vality.mapper.RecordRowMapper;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;

import static dev.vality.alerting.mayday.domain.tables.MetricTemplate.METRIC_TEMPLATE;

@Component
public class MetricTemplateDaoImpl extends AbstractDao implements MetricTemplateDao {

    private final RowMapper<MetricTemplate> listRecordRowMapper;

    public MetricTemplateDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(METRIC_TEMPLATE, MetricTemplate.class);
    }

    @Override
    public List<MetricTemplate> findAll() {
        Query query = getDslContext().selectFrom(METRIC_TEMPLATE);
        return fetch(query, listRecordRowMapper);
    }

    @Override
    public MetricTemplate findById(Long metricTemplateId) {
        Query query = getDslContext().selectFrom(METRIC_TEMPLATE)
                .where(METRIC_TEMPLATE.ID.eq(metricTemplateId));
        return fetchOne(query, listRecordRowMapper);
    }
}
