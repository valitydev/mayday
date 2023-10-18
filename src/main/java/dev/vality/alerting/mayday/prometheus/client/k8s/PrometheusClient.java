package dev.vality.alerting.mayday.prometheus.client.k8s;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.prometheus.client.k8s.util.PrometheusFunctionsUtil;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

@Slf4j
@Component
@RequiredArgsConstructor
public class PrometheusClient {

    private final Config k8sConfig;

    public void createPrometheusRule(PrometheusRule prometheusRule) throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            try {
                prometheusRuleClient.inNamespace(client.getNamespace()).resource(prometheusRule).create();
            } catch (KubernetesClientException e) {
                // 409 http код возникает при попытке создать уже существующий объект
                if (!e.getStatus().getCode().equals(HttpStatus.CONFLICT.value())) {
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
        modifyPrometheusRule(ruleName, PrometheusFunctionsUtil.getRemoveGroupByNameFunc(groupName));
    }

    public void deleteAlertFromPrometheusRuleGroup(String ruleName, String groupName, String alertNameForRemoval)
            throws KubernetesClientException {
        modifyPrometheusRule(ruleName, PrometheusFunctionsUtil.getRemoveAlertByGroupAndNameFunc(groupName,
                alertNameForRemoval));
    }

    public void addAlertToPrometheusRuleGroup(String ruleName, String groupName, PrometheusRuleSpec.Rule alert)
            throws KubernetesClientException {
        modifyPrometheusRule(ruleName, PrometheusFunctionsUtil.getAddAlertToGroupFunc(groupName, alert));
    }

    private void modifyPrometheusRule(String ruleName,
                                      UnaryOperator<PrometheusRule> modifyFunc) throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<PrometheusRule, KubernetesResourceList<PrometheusRule>, Resource<PrometheusRule>>
                    prometheusRuleClient = client.resources(PrometheusRule.class);
            var rule =
                    prometheusRuleClient.inNamespace(client.getNamespace()).withName(ruleName).get();
            var resource = prometheusRuleClient.inNamespace(client.getNamespace()).resource(rule);
            var response = resource.edit(modifyFunc);
            log.info("Rule after modification: {}", response);
        }
    }


}
