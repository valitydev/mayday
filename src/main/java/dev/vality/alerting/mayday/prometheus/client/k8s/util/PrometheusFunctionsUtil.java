package dev.vality.alerting.mayday.prometheus.client.k8s.util;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@UtilityClass
public class PrometheusFunctionsUtil {

    public static UnaryOperator<PrometheusRule> getRemoveGroupByNameFunc(String groupName) {
        return prometheusRule -> {
            var groups = prometheusRule.getSpec().getGroups();
            var groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                var group = groupIterator.next();
                if (group.getName().equals(groupName)) {
                    groupIterator.remove();
                    break;
                }
            }
            return prometheusRule;
        };
    }

    public static UnaryOperator<PrometheusRule> getRemoveAlertByGroupAndNameFunc(String groupName,
                                                                                 String alertNameForRemoval) {
        return prometheusRule -> {
            var groups = prometheusRule.getSpec().getGroups();
            var groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                var group = groupIterator.next();
                if (group.getName().equals(groupName)) {
                    Set<PrometheusRuleSpec.Rule> alertRules = group.getRules();
                    var ruleIterator = alertRules.iterator();
                    while (ruleIterator.hasNext()) {
                        var rule = ruleIterator.next();
                        if (rule.getAlert().equals(alertNameForRemoval)) {
                            ruleIterator.remove();
                            break;
                        }
                    }
                    if (group.getRules().isEmpty()) {
                        groupIterator.remove();
                    }
                    break;
                }
            }
            return prometheusRule;
        };
    }

    public static UnaryOperator<PrometheusRule> getAddAlertToGroupFunc(String groupName,
                                                                       PrometheusRuleSpec.Rule alert) {
        return prometheusRule -> {
            var groups = prometheusRule.getSpec().getGroups();
            if (groups == null) {
                groups = new HashSet<>();
                prometheusRule.getSpec().setGroups(groups);
            }
            PrometheusRuleSpec.Group group = groups.stream().filter(g -> g.getName().equals(groupName)).findFirst()
                    .orElse(createPrometheusRuleGroup(groupName));
            groups.add(group);
            var rules = group.getRules();
            if (rules == null) {
                rules = new HashSet<>();
                group.setRules(rules);
            }
            rules.add(alert);
            return prometheusRule;
        };
    }

    public static PrometheusRuleSpec.Group createPrometheusRuleGroup(String groupName) {
        var group = new PrometheusRuleSpec.Group();
        group.setName(groupName);
        group.setRules(new HashSet<>());
        return group;
    }

}
