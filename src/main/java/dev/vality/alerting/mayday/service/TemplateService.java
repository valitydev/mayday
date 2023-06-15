package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.dao.AlertParamDao;
import dev.vality.alerting.mayday.dao.AlertTemplateDao;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final AlertTemplateDao alertTemplateDao;
    private final AlertParamDao alertParamDao;

    public AlertTemplate getAlertTemplateById(String metricTemplateId) {
        return alertTemplateDao.findById(metricTemplateId);
    }

    public List<AlertTemplate> getAlertTemplates() {
        return alertTemplateDao.findAll();
    }

    public List<AlertParam> getAlertTemplateParams(String templateId) {
        return alertParamDao.findAllByAlertTemplateId(templateId);
    }


}
