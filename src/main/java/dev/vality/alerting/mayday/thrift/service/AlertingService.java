package dev.vality.alerting.mayday.thrift.service;

import dev.vality.alerting.mayday.*;
import dev.vality.alerting.mayday.alertmanager.service.AlertmanagerService;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.AlertTemplate;
import dev.vality.alerting.mayday.prometheus.service.PrometheusService;
import dev.vality.alerting.mayday.alerttemplate.service.helper.TemplateHelper;
import dev.vality.alerting.mayday.alerttemplate.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertingService implements AlertingServiceSrv.Iface {

    private final TemplateService templateService;
    private final PrometheusService prometheusService;
    private final AlertmanagerService alertmanagerService;

    private final TemplateHelper templateHelper;

    private final Converter<List<AlertTemplate.AlertConfigurationParameter>, AlertConfiguration>
            alertParamsToAlertConfiguration;

    @Override
    public void deleteAlert(DeleteAlertRequest deleteAlertRequest) throws AlertNotFound {
        String userAlertId = deleteAlertRequest.getUserAlertId();
        String userId = deleteAlertRequest.getUserId();
        log.info("Removing alert '{}' for user '{}'", userAlertId, userId);
        alertmanagerService.deleteUserRoute(userAlertId);
        prometheusService.deleteUserAlert(userId, userAlertId);
        log.info("Removed alert '{}' for user '{}'", userAlertId, userId);
    }

    @Override
    public List<UserAlert> getUserAlerts(GetUserAlertsRequest getUserAlertsRequest) throws UserNotFound {
        String userId = getUserAlertsRequest.getUserId();
        log.info("Retrieving all alerts for user '{}'", userId);
        List<UserAlert> userAlerts = prometheusService.getUserAlerts(userId);
        log.info("Retrieved {} alerts for user '{}'", userAlerts.size(), userId);
        return userAlerts;
    }

    @Override
    public List<AlertConfiguration> getAlertConfigurationsList() {
        log.info("Retrieving all alert configurations");
        List<AlertConfiguration> alertConfigurations = templateService.getAlertTemplates().stream()
                .sorted(Comparator.comparing(AlertTemplate::getReadableName))
                .map(alertTemplate -> {
                    AlertConfiguration alertConfiguration =
                            alertParamsToAlertConfiguration.convert(alertTemplate.getParameters());
                    alertConfiguration.setId(alertTemplate.getId());
                    alertConfiguration.setName(alertTemplate.getReadableName());
                    return alertConfiguration;
                })
                .collect(Collectors.toList());
        log.info("Retrieved {} alert configurations", alertConfigurations.size());
        return alertConfigurations;
    }

    @Override
    public void createAlert(CreateAlertRequest createAlertRequest) {
        log.info("Processing CreateAlertRequest: '{}'", createAlertRequest);
        List<AlertTemplate.AlertConfigurationParameter> metricParams =
                templateService.getAlertTemplateParams(createAlertRequest.getAlertId());
        AlertTemplate metricTemplate =
                templateService.getAlertTemplateById(createAlertRequest.getAlertId());
        CreateAlertDto createAlertDto =
                templateHelper.preparePrometheusRuleData(createAlertRequest, metricTemplate, metricParams);
        prometheusService.createUserAlert(createAlertDto);
        alertmanagerService.createUserRoute(createAlertDto);
        log.info("CreateAlertRequest processed successfully: '{}'", createAlertRequest);
    }

}
