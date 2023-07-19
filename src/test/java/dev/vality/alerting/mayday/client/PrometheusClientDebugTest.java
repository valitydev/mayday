package dev.vality.alerting.mayday.client;

import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled("For local client testing")
class PrometheusClientDebugTest {

    private final Config config = new ConfigBuilder().withNamespace("default").build();
    private final PrometheusClient client = new PrometheusClient(config);
    private final String ruleName = "testrule";
    private final String groupName = "testGroup";
    private final String alertName = "unittest_alert";

    @Test
    void createPrometheusRule() {
        client.createPrometheusRule(createTestPrometheusRule());
    }

    @Test
    void getPrometheusRule() {
        Optional<PrometheusRule> rule = client.getPrometheusRule(ruleName);
        assertTrue(rule.isEmpty());
        createPrometheusRule();
        rule = client.getPrometheusRule(ruleName);
        assertTrue(rule.isPresent());
    }

    @Test
    void getPrometheusRuleGroupAlerts() {
        addAlertToPrometheusRuleGroup();
        var alerts = client.getPrometheusRuleGroupAlerts(ruleName, groupName);
        assertEquals(1, alerts.size());
    }

    @Test
    void deletePrometheusRuleGroup() {
        createPrometheusRule();
        addAlertToPrometheusRuleGroup();
        client.deletePrometheusRuleGroup(ruleName, groupName);
    }

    @Test
    void deleteAlertFromPrometheusRuleGroup() {
        createPrometheusRule();
        addAlertToPrometheusRuleGroup();
        client.deleteAlertFromPrometheusRuleGroup(ruleName, groupName, ruleName);
    }

    @Test
    void addAlertToPrometheusRuleGroup() {
        PrometheusRuleSpec.Rule rule = new PrometheusRuleSpec.Rule();
        rule.setAlert(alertName);
        rule.setExpr("vector(1)");
        rule.setDuration("5m");
        rule.setAnnotations(Map.of("readable_name", "тестовый алерт"));
        client.addAlertToPrometheusRuleGroup(ruleName, groupName, rule);
    }

    private PrometheusRule createTestPrometheusRule() {
        PrometheusRule rule = new PrometheusRule();
        rule.setApiVersion("monitoring.coreos.com/v1");
        rule.setKind("PrometheusRule");
        var metadata = new ObjectMeta();
        metadata.setLabels(Map.of("prometheus", "prometheus"));
        metadata.setName(ruleName);
        rule.setMetadata(metadata);
        PrometheusRuleSpec spec = new PrometheusRuleSpec();
        rule.setSpec(spec);
        return rule;
    }
}