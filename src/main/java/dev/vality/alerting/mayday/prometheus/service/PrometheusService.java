package dev.vality.alerting.mayday.prometheus.service;

import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.prometheus.config.properties.K8sPrometheusRuleProperties;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class PrometheusService {

    @Value("${spring.application.name}")
    private String applicationName;
    private String prometheusRuleName;

    private final K8sPrometheusRuleProperties k8SPrometheusRuleProperties;
    private final PrometheusClient prometheusClient;
    private final Converter<CreateAlertDto, PrometheusRuleSpec.Rule> createAlertDtoToPrometheusRuleConverter;
    private final Converter<PrometheusRuleSpec.Rule, UserAlert> prometheusRuleToUserAlertConverter;

    public void deleteAllUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(getPrometheusRuleName()).isEmpty()) {
            return;
        }
        prometheusClient.deletePrometheusRuleGroup(getPrometheusRuleName(), userId);
    }

    public void deleteUserAlert(String userId, String alertId) {
        if (prometheusClient.getPrometheusRule(getPrometheusRuleName()).isEmpty()) {
            return;
        }
        prometheusClient.deleteAlertFromPrometheusRuleGroup(getPrometheusRuleName(), userId, alertId);
    }

    public List<UserAlert> getUserAlerts(String userId) {
        if (prometheusClient.getPrometheusRule(getPrometheusRuleName()).isEmpty()) {
            return List.of();
        }
        return prometheusClient.getPrometheusRuleGroupAlerts(getPrometheusRuleName(), userId).stream()
                .map(prometheusRuleToUserAlertConverter::convert)
                .collect(Collectors.toList());
    }

    public void createUserAlert(CreateAlertDto createAlertDto) {
        if (prometheusClient.getPrometheusRule(getPrometheusRuleName()).isEmpty()) {
            log.info("Prometheus rule '{}' not found and will be created", getPrometheusRuleName());
            prometheusClient.createPrometheusRule(buildPrometheusRule());
        }
        PrometheusRuleSpec.Rule alertRule = createAlertDtoToPrometheusRuleConverter.convert(createAlertDto);
        log.info("New alert configuration: {}", alertRule);
        prometheusClient.addAlertToPrometheusRuleGroup(getPrometheusRuleName(), createAlertDto.getUserId(), alertRule);
    }

    public String getPrometheusRuleName() {
        if (ObjectUtils.isEmpty(prometheusRuleName)) {
            prometheusRuleName = "%s-managed-rule".formatted(applicationName);
        }
        return prometheusRuleName;
    }

    private PrometheusRule buildPrometheusRule() {
        PrometheusRule rule = new PrometheusRule();
        var metadata = new ObjectMeta();
        metadata.setLabels(k8SPrometheusRuleProperties.getLabels());
        metadata.setName(getPrometheusRuleName());
        rule.setMetadata(metadata);
        PrometheusRuleSpec spec = new PrometheusRuleSpec();
        rule.setSpec(spec);
        return rule;
    }

}
