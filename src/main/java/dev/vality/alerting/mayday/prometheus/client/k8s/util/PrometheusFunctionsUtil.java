package dev.vality.alerting.mayday.prometheus.client.k8s.util;

import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Slf4j
@UtilityClass
public class PrometheusFunctionsUtil {

    public static UnaryOperator<PrometheusRule> getRemoveGroupByNameFunc(String groupName) {
        return prometheusRule -> {
            var groups = prometheusRule.getSpec().getGroups();

            if (ObjectUtils.isEmpty(groups)) {
                log.info("Groups are empty. No action required.");
                return prometheusRule;
            }

            groups.removeIf(group -> group.getName().equals(groupName));
            return prometheusRule;
        };
    }

    public static UnaryOperator<PrometheusRule> getRemoveAlertByGroupAndNameFunc(String groupName,
                                                                                 String alertNameForRemoval) {
        return prometheusRule -> {
            log.info("Rule before removal: {}", prometheusRule);
            log.info("Going to remove alert '{}' for user '{}'", alertNameForRemoval, groupName);
            var groups = prometheusRule.getSpec().getGroups();

            if (ObjectUtils.isEmpty(groups)) {
                log.info("Groups are empty. No action required.");
                return prometheusRule;
            }

            var groupIterator = groups.iterator();
            while (groupIterator.hasNext()) {
                var group = groupIterator.next();
                log.info("Found user '{}'...", group.getName());
                List<PrometheusRuleSpec.Rule> alertRules = group.getRules();
                var ruleIterator = alertRules.iterator();
                while (ruleIterator.hasNext()) {
                    var rule = ruleIterator.next();
                    log.info("Found rule '{}' for user '{}'...", rule.getAlert(), group.getName());
                    if (rule.getAlert().equals(alertNameForRemoval)
                            && rule.getLabels().get(PrometheusRuleLabel.USERNAME).equals(groupName)) {

                        ruleIterator.remove();
                        log.info("Rule '{}' for user '{}' will be removed!", rule.getAlert(), group.getName());

                        if (group.getRules().isEmpty()) {
                            log.info("User '{}'has no more rules and will be removed!", group.getName());
                            groupIterator.remove();
                            break;
                        }
                    }
                }
            }
            log.info("Rule after removal: {}", prometheusRule);
            return prometheusRule;
        };
    }

    public static UnaryOperator<PrometheusRule> getAddAlertToGroupFunc(String groupName,
                                                                       PrometheusRuleSpec.Rule alert) {
        return prometheusRule -> {
            var groups = prometheusRule.getSpec().getGroups();
            if (ObjectUtils.isEmpty(groups)) {
                groups = new ArrayList<>();
                prometheusRule.getSpec().setGroups(groups);
            }

            var groupIterator = groups.iterator();
            PrometheusRuleSpec.Group group = null;
            while (groupIterator.hasNext()) {
                var cursor = groupIterator.next();
                log.info("Found group with name '{}'...", cursor.getName());
                if (cursor.getName().equals(groupName)) {
                    log.info("Found matching group with name '{}'! Group: {}", cursor.getName(), cursor);
                    group = cursor;
                    break;
                }
            }

            if (ObjectUtils.isEmpty(group)) {
                group = createPrometheusRuleGroup(groupName);
                groups.add(group);
            }
            var rules = group.getRules();
            if (rules == null) {
                rules = new ArrayList<>();
                group.setRules(rules);
            }
            log.info("Adding alert '{}' to group '{}'", alert, group);
            for (var rule : rules) {
                if (rule.getAlert().equals(alert.getAlert())) {
                    log.info("Alert '{}' already exists and won't be added", rule);
                    return prometheusRule;
                }
            }
            rules.add(alert);
            return prometheusRule;
        };
    }

    public static PrometheusRuleSpec.Group createPrometheusRuleGroup(String groupName) {
        log.info("Creating group with name '{}'", groupName);
        var group = new PrometheusRuleSpec.Group();
        group.setName(groupName);
        group.setRules(new ArrayList<>());
        return group;
    }

}
