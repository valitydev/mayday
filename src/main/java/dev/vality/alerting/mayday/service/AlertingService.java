package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.*;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlertingService implements AlertingServiceSrv.Iface {

    private final MetricTemplateService metricConfigurationService;
    private final PrometheusService prometheusService;
    private final AlertmanagerService alertmanagerService;

    @Override
    public void deleteAllAlerts(String userId) throws TException {
        alertmanagerService.deleteAllUserRoutes(userId);
        prometheusService.deleteAllUserAlerts(userId);
    }

    @Override
    public void deleteAlert(String userId, String alertId) throws TException {
        prometheusService.deleteUserAlert(userId, alertId);
        alertmanagerService.deleteUserRoute(userId, alertId);
    }

    @Override
    public List<UserAlert> getUserAlerts(String userId) throws TException {
        Set<PrometheusRuleSpec.Rule> prometheusAlerts = prometheusService.getUserAlerts(userId);
        return null; //TODO: convert
    }

    @Override
    public List<Alert> getSupportedAlerts() throws TException {
        return metricConfigurationService.getAllMetricTemplates();
    }

    @Override
    public AlertConfiguration getAlertConfiguration(String alertTemplateId) throws TException {
        return metricConfigurationService.getTemplateConfiguration(alertTemplateId).orElseThrow(AlertNotFound::new);
    }

    @Override
    public void createAlert(CreateAlertRequest createAlertRequest) throws
            TException {
        Optional<AlertConfiguration> alertConfigurationOptional =
                metricConfigurationService.getTemplateConfiguration(createAlertRequest.getAlertId());
        if(alertConfigurationOptional.isEmpty()) {
            throw new AlertNotFound();
        }
        AlertConfiguration alertConfiguration = alertConfigurationOptional.get();
        List<MetricParam> metricParams = metricConfigurationService.getMetricParams(createAlertRequest.getAlertId());
        var rule = buildPrometheusRule(createAlertRequest, alertConfiguration, metricParams);
        prometheusService.createUserAlert(createAlertRequest.getUserId(), rule);
    }

    private PrometheusRuleSpec.Rule buildPrometheusRule(CreateAlertRequest createAlertRequest,
                                                        AlertConfiguration alertConfiguration,
                                                        List<MetricParam> metricParams) {
        return null;
    }
}
