package dev.vality.alerting.mayday.client;

import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRule;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.client.util.K8sUtil;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import static dev.vality.alerting.mayday.client.error.ErrorCodes.ALREADY_EXISTS_ERROR_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class K8sPrometheusClient {

    private final Config k8sConfig;

    public void createPrometheusRule(PrometheusRule prometheusRule) throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            try {
                var result = prometheusRuleClient.inNamespace(client.getNamespace()).resource(prometheusRule).create();
                System.out.println(result);
            } catch (KubernetesClientException e) {
                if (!e.getStatus().getCode().equals(ALREADY_EXISTS_ERROR_CODE)) {
                    throw e;
                }
                log.warn("Tried to create already existing rule", e);
            }
        }
    }

    public Optional<PrometheusRule> getPrometheusRule(String ruleName) throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            var rule = prometheusRuleClient.withName(ruleName).get();
            return Optional.ofNullable(rule);
        }
    }

    public Set<PrometheusRuleSpec.Rule> getPrometheusRuleGroupAlerts(String ruleName,
                                                                     String groupName)
            throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            client.close();
            var rule =
                    prometheusRuleClient.inNamespace(client.getNamespace()).withName(ruleName).get();
            if (rule == null) {
                return Set.of();
            }
            var groupAlerts = rule.getSpec().getGroups().stream()
                    .filter(group -> group.getName().equals(groupName))
                    .findFirst().orElse(new PrometheusRuleSpec.Group()).getRules();
            return groupAlerts == null ? Set.of() : groupAlerts;
        }
    }

    public void deletePrometheusRuleGroup(String ruleName, String groupName) throws KubernetesClientException {
        modifyPrometheusRule(ruleName, K8sUtil.getRemoveGroupByNameFunc(groupName));
    }

    public void deleteAlertFromPrometheusRuleGroup(String ruleName, String groupName, String alertNameForRemoval)
            throws KubernetesClientException {
        modifyPrometheusRule(ruleName, K8sUtil.getRemoveAlertByGroupAndNameFunc(groupName, alertNameForRemoval));
    }

    public void addAlertToPrometheusRuleGroup(String ruleName, String groupName, PrometheusRuleSpec.Rule alert)
            throws KubernetesClientException {
        modifyPrometheusRule(ruleName, K8sUtil.getAddAlertToGroupFunc(groupName, alert));
    }

    private void modifyPrometheusRule(String ruleName,
                                      UnaryOperator<PrometheusRule> modifyFunc) throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            var rule =
                    prometheusRuleClient.inNamespace(client.getNamespace()).withName(ruleName).get();
            var resource = prometheusRuleClient.inNamespace(client.getNamespace()).resource(rule);
            resource.edit(modifyFunc);
        }
    }


}
