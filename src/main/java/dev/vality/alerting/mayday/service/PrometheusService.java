package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.client.K8sPrometheusClient;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.vality.alerting.mayday.constant.K8sParameter.PROMETHEUS_RULE_NAME;

@Service
@RequiredArgsConstructor
public class PrometheusService {

    private final K8sPrometheusClient k8SPrometheusClient;
    private final Converter<CreateAlertDto, PrometheusRuleSpec.Rule> createAlertDtoToPrometheusRuleConverter;
    private final Converter<PrometheusRuleSpec.Rule, UserAlert> prometheusRuleToUserAlertConverter;

    public void deleteAllUserAlerts(String userId) {
        k8SPrometheusClient.deletePrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId);
    }

    public void deleteUserAlert(String userId, String alertId) {
        k8SPrometheusClient.deleteAlertFromPrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId, alertId);
    }

    public List<UserAlert> getUserAlerts(String userId) {
        return k8SPrometheusClient.getPrometheusRuleGroupAlerts(PROMETHEUS_RULE_NAME, userId).stream()
                .map(prometheusRuleToUserAlertConverter::convert)
                .collect(Collectors.toList());
    }

    public void createUserAlert(CreateAlertDto createAlertDto) {
        PrometheusRuleSpec.Rule alertRule = createAlertDtoToPrometheusRuleConverter.convert(createAlertDto);
        k8SPrometheusClient.addAlertToPrometheusRuleGroup(PROMETHEUS_RULE_NAME, createAlertDto.getUserId(), alertRule);
    }


}
