package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;

import java.util.List;

public interface MetricParamDao {

    List<MetricParam> findAllByMetricTemplateId(Long metricTemplateId);
}
