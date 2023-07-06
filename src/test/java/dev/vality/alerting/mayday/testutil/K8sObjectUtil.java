package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class K8sObjectUtil {

    public static PrometheusRuleSpec.Rule testPrometheusRule() {
        var rule =  new PrometheusRuleSpec.Rule();
        rule.setAlert("test_alert");
        rule.setDuration("5m");
        rule.setExpr("vector(1)");
        rule.setAnnotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, "тестовый алерт"));
        return rule;
    }
}
