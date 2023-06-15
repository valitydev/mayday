package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;

import java.util.List;

public interface AlertParamDao {

    List<AlertParam> findAllByAlertTemplateId(String metricTemplateId);
}
