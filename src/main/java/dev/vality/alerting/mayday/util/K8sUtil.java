package dev.vality.alerting.mayday.util;

import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfig;
import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRule;
import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

@UtilityClass
public class K8sUtil {

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
            for (PrometheusRuleSpec.Group group : groups) {
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

    public static UnaryOperator<AlertmanagerConfig> getRemoveReceiverByNameFunc(String receiverName) {
        return alertmanagerConfig -> {
            var receivers = alertmanagerConfig.getSpec().getRoute().getRoutes();
            if (receivers == null || receivers.isEmpty()) {
                return alertmanagerConfig;
            }
            var receiverIterator = receivers.iterator();
            while (receiverIterator.hasNext()) {
                var receiver = receiverIterator.next();
                if (receiver.getReceiver().equals(receiverName)) {
                    receiverIterator.remove();
                    break;
                }
            }
            return alertmanagerConfig;
        };
    }

    public static UnaryOperator<AlertmanagerConfig> getAddReceiverFunc(AlertmanagerConfigSpec.Receiver receiver) {
        return alertmanagerConfig -> {
            alertmanagerConfig.getSpec().getRoute().getRoutes().add(receiver);
            return alertmanagerConfig;
        };
    }
}
