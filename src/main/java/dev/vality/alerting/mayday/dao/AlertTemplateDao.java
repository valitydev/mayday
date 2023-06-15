package dev.vality.alerting.mayday.dao;

import dev.vality.alerting.mayday.domain.tables.pojos.AlertTemplate;

import java.util.List;

public interface AlertTemplateDao {

    List<AlertTemplate> findAll();

    AlertTemplate findById(String id);
}
