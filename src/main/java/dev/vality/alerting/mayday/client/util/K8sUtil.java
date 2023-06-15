package dev.vality.alerting.mayday.client.util;

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
            //TODO: do not add if already exists?
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

    public static UnaryOperator<AlertmanagerConfig> getRemoveRouteByNameFunc(String userId, String alertId) {
        return alertmanagerConfig -> {
            var routes = alertmanagerConfig.getSpec().getRoute().getRoutes();
            if (routes == null || routes.isEmpty()) {
                return alertmanagerConfig;
            }
            var routesIterator = routes.iterator();
            while (routesIterator.hasNext()) {
                var route = routesIterator.next();
                route.setMatchers();
                if (receiverName.equals(receiver.getReceiver())) {
                    routesIterator.remove();
                    break;
                }
            }
            return alertmanagerConfig;
        };
    }

    public static UnaryOperator<AlertmanagerConfig> getAddReceiverAndRouteFunc(AlertmanagerConfigSpec.ChildRoute route,
                                                                               AlertmanagerConfigSpec.Receiver receiver) {
        return alertmanagerConfig -> {

            if (!hasReceiver(alertmanagerConfig, receiver)) {
                alertmanagerConfig.getSpec().getReceivers().add(receiver);
            }

            if (!hasRoute(alertmanagerConfig, route)) {
                alertmanagerConfig.getSpec().getRoute().getRoutes().add(route);
            }

            return alertmanagerConfig;
        };
    }

    private static boolean hasReceiver(AlertmanagerConfig alertmanagerConfig,
                                       AlertmanagerConfigSpec.Receiver receiver) {
        return alertmanagerConfig.getSpec().getReceivers().stream()
                .anyMatch(configReceiver -> configReceiver.getName().equals(receiver.getName()));
    }

    private static boolean hasRoute(AlertmanagerConfig alertmanagerConfig, AlertmanagerConfigSpec.ChildRoute route) {
        return alertmanagerConfig.getSpec().getRoute().getRoutes().stream()
                .anyMatch(childRoute -> childRoute.equals(route));
    }
}
