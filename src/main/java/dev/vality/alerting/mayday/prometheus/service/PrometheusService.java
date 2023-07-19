package dev.vality.alerting.mayday.prometheus.service;

import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.common.config.properties.KubernetesProperties;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static dev.vality.alerting.mayday.constant.K8sParameter.PROMETHEUS_RULE_NAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusService {

    public static final String PROMETHEUS_RULE_NAME = "mayday-managed-rule";

    private final KubernetesProperties kubernetesProperties;
    private final PrometheusClient prometheusClient;
    private final Converter<CreateAlertDto, PrometheusRuleSpec.Rule> createAlertDtoToPrometheusRuleConverter;
    private final Converter<PrometheusRuleSpec.Rule, UserAlert> prometheusRuleToUserAlertConverter;

    public void deleteAllUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(PROMETHEUS_RULE_NAME).isEmpty()) {
            return;
        }
        prometheusClient.deletePrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId);
    }

    public void deleteUserAlert(String userId, String alertId) {
        if (prometheusClient.getPrometheusRule(PROMETHEUS_RULE_NAME).isEmpty()) {
            return;
        }
        prometheusClient.deleteAlertFromPrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId, alertId);
    }

    public List<UserAlert> getUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(PROMETHEUS_RULE_NAME).isEmpty()) {
            return List.of();
        }
        return prometheusClient.getPrometheusRuleGroupAlerts(PROMETHEUS_RULE_NAME, userId).stream()
                .map(prometheusRuleToUserAlertConverter::convert)
                .collect(Collectors.toList());
    }

    public void createUserAlert(CreateAlertDto createAlertDto) {
        if (prometheusClient.getPrometheusRule(PROMETHEUS_RULE_NAME).isEmpty()) {
            log.info("Prometheus rule '{}' not found and will be created", PROMETHEUS_RULE_NAME);
            prometheusClient.createPrometheusRule(buildPrometheusRule());
        }
        PrometheusRuleSpec.Rule alertRule = createAlertDtoToPrometheusRuleConverter.convert(createAlertDto);
        log.info("New alert configuration: {}", alertRule);
        prometheusClient.addAlertToPrometheusRuleGroup(PROMETHEUS_RULE_NAME, createAlertDto.getUserId(), alertRule);
    }

    private PrometheusRule buildPrometheusRule() {
        PrometheusRule rule = new PrometheusRule();
        var metadata = new ObjectMeta();
        metadata.setLabels(kubernetesProperties.getPrometheusRule().getLabels());
        metadata.setName(PROMETHEUS_RULE_NAME);
        rule.setMetadata(metadata);
        PrometheusRuleSpec spec = new PrometheusRuleSpec();
        rule.setSpec(spec);
        return rule;
    }

}
