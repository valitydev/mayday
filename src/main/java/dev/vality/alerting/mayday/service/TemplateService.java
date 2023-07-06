package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.error.AlertTemplateNotFoundException;
import dev.vality.alerting.mayday.model.alerttemplate.AlertTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final Map<String, AlertTemplate> alertConfigurations;

    public AlertTemplate getAlertTemplateById(String metricTemplateId) {
        return alertConfigurations.get(metricTemplateId);
    }

    public List<AlertTemplate> getAlertTemplates() {
        return new ArrayList<>(alertConfigurations.values());
    }

    public List<AlertTemplate.AlertConfigurationParameter> getAlertTemplateParams(String templateId) {
        if (!alertConfigurations.containsKey(templateId)) {
            throw new AlertTemplateNotFoundException(String.format("Unable to find templateId '%s'", templateId));
        }
        return alertConfigurations.get(templateId).getParameters();
    }


}
