package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.*;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.converter.CreateAlertDtoToPrometheusRuleConverter;
import dev.vality.alerting.mayday.converter.MetricParamsToAlertConfiguration;
import dev.vality.alerting.mayday.converter.MetricTemplateToAlertConverter;
import dev.vality.alerting.mayday.converter.PrometheusRuleToUserAlertConverter;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.service.helper.MetricTemplateHelper;
import lombok.RequiredArgsConstructor;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlertingService implements AlertingServiceSrv.Iface {

    private final MetricTemplateService metricConfigurationService;
    private final PrometheusService prometheusService;
    private final AlertmanagerService alertmanagerService;

    //TODO: use converters better
    private final PrometheusRuleToUserAlertConverter prometheusRuleToUserAlertConverter;
    private final MetricTemplateToAlertConverter metricTemplateToAlertConverter;
    private final MetricParamsToAlertConfiguration metricParamsToAlertConfiguration;
    private final CreateAlertDtoToPrometheusRuleConverter createAlertDtoToPrometheusRuleConverter;

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
        return prometheusAlerts.stream().map(prometheusRuleToUserAlertConverter::convert).collect(Collectors.toList());
    }

    @Override
    public List<Alert> getSupportedAlerts() throws TException {
        List<MetricTemplate> metricTemplates = metricConfigurationService.getAllMetricTemplates();
        return metricTemplates.stream().map(metricTemplateToAlertConverter::convert).collect(Collectors.toList());
    }

    @Override
    public AlertConfiguration getAlertConfiguration(String alertTemplateId) throws TException {
        List<MetricParam> metricParams = metricConfigurationService.getMetricParams(alertTemplateId);
        AlertConfiguration alertConfiguration = metricParamsToAlertConfiguration.convert(metricParams);
        alertConfiguration.setId(alertTemplateId);
        return alertConfiguration;
    }

    @Override
    public void createAlert(CreateAlertRequest createAlertRequest) throws
            TException {
        List<MetricParam> metricParams = metricConfigurationService.getMetricParams(createAlertRequest.getAlertId());
        MetricTemplate metricTemplate =
                metricConfigurationService.getMetricTemplateById(Long.valueOf(createAlertRequest.getAlertId()));
        CreateAlertDto createAlertDto =
                MetricTemplateHelper.preparePrometheusRuleData(createAlertRequest, metricTemplate, metricParams);
        var prometheusRule = createAlertDtoToPrometheusRuleConverter.convert(createAlertDto);

        prometheusService.createUserAlert(createAlertDto.getUserId(), prometheusRule);
        //TODO: fix
        alertmanagerService.createUserRoute(createAlertDto.getUserId(), null);

    }

}
