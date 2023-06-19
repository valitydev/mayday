package dev.vality.alerting.mayday.dao.impl;

import dev.vality.alerting.mayday.dao.AlertParamDao;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;
import dev.vality.mapper.RecordRowMapper;
import org.jooq.Query;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.UUID;

import static dev.vality.alerting.mayday.domain.tables.AlertParam.ALERT_PARAM;
import static dev.vality.alerting.mayday.domain.tables.AlertTemplatesToAlertParams.ALERT_TEMPLATES_TO_ALERT_PARAMS;

@Component
public class AlertParamDaoImpl extends AbstractDao implements AlertParamDao {

    private final RowMapper<AlertParam> listRecordRowMapper;

    public AlertParamDaoImpl(DataSource dataSource) {
        super(dataSource);
        listRecordRowMapper = new RecordRowMapper<>(ALERT_PARAM, AlertParam.class);
    }

    public List<AlertParam> findAllByAlertTemplateId(String alertTemplateId) {
        Query query =
                getDslContext()
                        .selectFrom(ALERT_PARAM)
                        .where(ALERT_PARAM.ID.in(
                                getDslContext().select(ALERT_TEMPLATES_TO_ALERT_PARAMS.ALERT_PARAM_ID)
                                        .from(ALERT_TEMPLATES_TO_ALERT_PARAMS)
                                        .where(ALERT_TEMPLATES_TO_ALERT_PARAMS.ALERT_TEMPLATE_ID
                                                .eq(UUID.fromString(alertTemplateId)))));
        //TODO: Проверить сценарий при 0 записей (необходимо бросать исключение)
        return fetch(query, listRecordRowMapper);
    }
}
