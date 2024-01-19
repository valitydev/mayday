package dev.vality.alerting.mayday.prometheus.client.k8s.util;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@Slf4j
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
            log.info("Going to remove alert '{}' for user '{}'", alertNameForRemoval, groupName);
            var groups = prometheusRule.getSpec().getGroups();
            var groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                var group = groupIterator.next();
                log.info("Found user '{}'...", group.getName());
                if (group.getName().equals(groupName)) {
                    log.info("User '{}' was found!", group.getName());
                    Set<PrometheusRuleSpec.Rule> alertRules = group.getRules();
                    var ruleIterator = alertRules.iterator();
                    while (ruleIterator.hasNext()) {
                        var rule = ruleIterator.next();
                        log.info("Found rule '{}' for user '{}'...", rule.getAlert(), group.getName());
                        if (rule.getAlert().equals(alertNameForRemoval)) {
                            ruleIterator.remove();
                            log.info("Rule '{}' for user '{}' will be removed!", rule.getAlert(), group.getName());
                            break;
                        }
                    }
                    if (group.getRules().isEmpty()) {
                        log.info("User '{}'has no more rules and will be removed!", group.getName());
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
            log.info("Adding alert '{}' to group '{}'", alert, group);
            rules.add(alert);
            return prometheusRule;
        };
    }

    public static PrometheusRuleSpec.Group createPrometheusRuleGroup(String groupName) {
        log.info("Creating group with name '{}'", groupName);
        var group = new PrometheusRuleSpec.Group();
        group.setName(groupName);
        group.setRules(new HashSet<>());
        return group;
    }

}
