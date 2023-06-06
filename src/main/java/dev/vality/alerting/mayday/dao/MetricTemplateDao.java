package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;

import java.util.List;

public interface MetricTemplateDao {

    List<MetricTemplate> findAll();

    MetricTemplate findById(Long metricTemplateId);
}
