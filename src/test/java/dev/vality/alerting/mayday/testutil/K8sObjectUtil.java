package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class K8sObjectUtil {

    public static PrometheusRuleSpec.Rule testPrometheusRule() {
        var rule =  new PrometheusRuleSpec.Rule();
        rule.setAlert("test_alert");
        rule.setExpr("vector(1)");
        rule.setAnnotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, "тестовый алерт"));
        return rule;
    }
}
