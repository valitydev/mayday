package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.client.K8sPrometheusClient;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static dev.vality.alerting.mayday.constant.K8sParameter.PROMETHEUS_RULE_NAME;

@Service
@RequiredArgsConstructor
public class PrometheusService {

    private final K8sPrometheusClient k8SPrometheusClient;

    public void deleteAllUserAlerts(String userId) {
        k8SPrometheusClient.deletePrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId);
    }

    public void deleteUserAlert(String userId, String alertId) {
        k8SPrometheusClient.deleteAlertFromPrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId, alertId);
    }

    public Set<PrometheusRuleSpec.Rule> getUserAlerts(String userId) {
        return k8SPrometheusClient.getPrometheusRuleGroupAlerts(PROMETHEUS_RULE_NAME, userId);
    }

    public void createUserAlert(String userId, PrometheusRuleSpec.Rule alert) {
        k8SPrometheusClient.addAlertToPrometheusRuleGroup(PROMETHEUS_RULE_NAME, userId, alert);
    }


}
