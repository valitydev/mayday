package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.*;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertTemplate;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.service.helper.TemplateHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertingService implements AlertingServiceSrv.Iface {

    private final TemplateService templateService;
    private final PrometheusService prometheusService;
    private final AlertmanagerService alertmanagerService;

    private final Converter<AlertTemplate, Alert> alertTemplateAlertConverter;
    private final Converter<List<AlertParam>, AlertConfiguration> alertParamsToAlertConfiguration;

    @Override
    public void deleteAllAlerts(String userId) {
        log.info("Removing all alerts for user '{}'", userId);
        alertmanagerService.deleteUserRoutes(userId);
        prometheusService.deleteAllUserAlerts(userId);
        log.info("Removed all alerts for user '{}'", userId);
    }

    @Override
    public void deleteAlert(String userId, String alertId) {
        log.info("Removing alert '{}' for user '{}'", alertId, userId);
        alertmanagerService.deleteUserRoute(userId, alertId);
        prometheusService.deleteUserAlert(userId, alertId);
        log.info("Removed alert '{}' for user '{}'", alertId, userId);
    }

    @Override
    public List<UserAlert> getUserAlerts(String userId) {
        log.info("Retrieving all alerts for user '{}'", userId);
        List<UserAlert> userAlerts = prometheusService.getUserAlerts(userId);
        log.info("Retrieved {} alerts for user '{}'", userAlerts.size(), userId);
        return userAlerts;
    }

    @Override
    public List<Alert> getSupportedAlerts() {
        log.info("Retrieving all supported alerts");
        List<AlertTemplate> metricTemplates = templateService.getAlertTemplates();
        List<Alert> supportedAlerts =
                metricTemplates.stream().map(alertTemplateAlertConverter::convert).collect(Collectors.toList());
        log.info("Retrieved {} supported alerts", supportedAlerts.size());
        return supportedAlerts;
    }

    @Override
    public AlertConfiguration getAlertConfiguration(String alertTemplateId) {
        log.info("Retrieving configuration for alert '{}'", alertTemplateId);
        List<AlertParam> metricParams = templateService.getAlertTemplateParams(alertTemplateId);
        AlertConfiguration alertConfiguration = alertParamsToAlertConfiguration.convert(metricParams);
        alertConfiguration.setId(alertTemplateId);
        log.info("Successfully retrieved configuration for alert '{}': {}", alertTemplateId, alertConfiguration);
        return alertConfiguration;
    }

    @Override
    public void createAlert(CreateAlertRequest createAlertRequest) {
        log.info("Processing CreateAlertRequest: '{}'", createAlertRequest);
        List<AlertParam> metricParams = templateService.getAlertTemplateParams(createAlertRequest.getAlertId());
        AlertTemplate metricTemplate =
                templateService.getAlertTemplateById(createAlertRequest.getAlertId());
        CreateAlertDto createAlertDto =
                TemplateHelper.preparePrometheusRuleData(createAlertRequest, metricTemplate, metricParams);
        prometheusService.createUserAlert(createAlertDto);
        alertmanagerService.createUserRoute(createAlertDto);
        log.info("CreateAlertRequest processed successfully: '{}'", createAlertRequest);
    }

}
