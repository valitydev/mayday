package dev.vality.alerting.mayday.prometheus.service;

import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.prometheus.config.properties.K8sPrometheusRuleProperties;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Getter
    private final String prometheusRuleName = "%s-managed-rule".formatted(applicationName);
    private final K8sPrometheusRuleProperties k8SPrometheusRuleProperties;
    private final PrometheusClient prometheusClient;
    private final Converter<CreateAlertDto, PrometheusRuleSpec.Rule> createAlertDtoToPrometheusRuleConverter;
    private final Converter<PrometheusRuleSpec.Rule, UserAlert> prometheusRuleToUserAlertConverter;

    public void deleteAllUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(prometheusRuleName).isEmpty()) {
            return;
        }
        prometheusClient.deletePrometheusRuleGroup(prometheusRuleName, userId);
    }

    public void deleteUserAlert(String userId, String alertId) {
        if (prometheusClient.getPrometheusRule(prometheusRuleName).isEmpty()) {
            return;
        }
        prometheusClient.deleteAlertFromPrometheusRuleGroup(prometheusRuleName, userId, alertId);
    }

    public List<UserAlert> getUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(prometheusRuleName).isEmpty()) {
            return List.of();
        }
        return prometheusClient.getPrometheusRuleGroupAlerts(prometheusRuleName, userId).stream()
                .map(prometheusRuleToUserAlertConverter::convert)
                .collect(Collectors.toList());
    }

    public void createUserAlert(CreateAlertDto createAlertDto) {
        if (prometheusClient.getPrometheusRule(prometheusRuleName).isEmpty()) {
            log.info("Prometheus rule '{}' not found and will be created", prometheusRuleName);
            prometheusClient.createPrometheusRule(buildPrometheusRule());
        }
        PrometheusRuleSpec.Rule alertRule = createAlertDtoToPrometheusRuleConverter.convert(createAlertDto);
        log.info("New alert configuration: {}", alertRule);
        prometheusClient.addAlertToPrometheusRuleGroup(prometheusRuleName, createAlertDto.getUserId(), alertRule);
    }

    private PrometheusRule buildPrometheusRule() {
        PrometheusRule rule = new PrometheusRule();
        var metadata = new ObjectMeta();
        metadata.setLabels(k8SPrometheusRuleProperties.getLabels());
        metadata.setName(prometheusRuleName);
        rule.setMetadata(metadata);
        PrometheusRuleSpec spec = new PrometheusRuleSpec();
        rule.setSpec(spec);
        return rule;
    }

}
