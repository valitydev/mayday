package dev.vality.alerting.mayday.dao.impl;

import dev.vality.alerting.mayday.dao.MetricParamDao;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import dev.vality.mapper.RecordRowMapper;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import java.util.List;

import static dev.vality.alerting.mayday.domain.tables.MetricParam.METRIC_PARAM;
import static dev.vality.alerting.mayday.domain.tables.MetricTemplatesToMetricParams.METRIC_TEMPLATES_TO_METRIC_PARAMS;

@Component
public class MetricParamDaoImpl extends AbstractDao implements MetricParamDao {

    private final RowMapper<MetricParam> listRecordRowMapper;

    public MetricParamDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(METRIC_PARAM, MetricParam.class);
    }

    public List<MetricParam> findAllByMetricTemplateId(Long metricTemplateId) {
        Query query =
                getDslContext()
                        .selectFrom(METRIC_PARAM)
                        .where(METRIC_PARAM.ID.in(
                                getDslContext().select(METRIC_TEMPLATES_TO_METRIC_PARAMS.METRIC_PARAM_ID)
                                        .from(METRIC_TEMPLATES_TO_METRIC_PARAMS)
                                        .where(METRIC_TEMPLATES_TO_METRIC_PARAMS.METRIC_TEMPLATE_ID
                                                .eq(metricTemplateId))));
        return fetch(query, listRecordRowMapper);
    }
}
