package dev.vality.alerting.mayday.alertmanager.client.k8s.util;

import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import lombok.experimental.UtilityClass;

import java.util.function.UnaryOperator;

@UtilityClass
public class AlertmanagerFunctionsUtil {

    public static UnaryOperator<AlertmanagerConfig> getRemoveRouteByAlertIdFunc(String alertId) {
        return alertmanagerConfig -> {
            var routes = alertmanagerConfig.getSpec().getRoute().getRoutes();
            if (routes == null || routes.isEmpty()) {
                return alertmanagerConfig;
            }
            var routesIterator = routes.iterator();
            while (routesIterator.hasNext()) {
                var route = routesIterator.next();
                var matchers = route.getMatchers();
                var alertNameMatcher = createMatcher(PrometheusRuleAnnotation.ALERT_NAME, alertId);
                if (matchers != null && matchers.contains(alertNameMatcher)) {
                    routesIterator.remove();
                    break;
                }
            }
            return alertmanagerConfig;
        };
    }

    public static AlertmanagerConfigSpec.Matcher createMatcher(String labelName, String labelValue) {
        AlertmanagerConfigSpec.Matcher matcher = new AlertmanagerConfigSpec.Matcher();
        matcher.setName(labelName);
        matcher.setValue(labelValue);
        matcher.setMatchType("=");
        matcher.setRegex(false);
        return matcher;
    }

    public static UnaryOperator<AlertmanagerConfig> getRemoveUserRoutesFunc(String userId) {
        return alertmanagerConfig -> {
            if (alertmanagerConfig.getSpec() == null) {
                return alertmanagerConfig;
            }

            var routes = alertmanagerConfig.getSpec().getRoute().getRoutes();
            if (routes == null || routes.isEmpty()) {
                return alertmanagerConfig;
            }
            var routesIterator = routes.iterator();
            while (routesIterator.hasNext()) {
                var route = routesIterator.next();
                var matchers = route.getMatchers();
                var userMatcher = createMatcher(PrometheusRuleLabel.USERNAME, userId);
                if (matchers.contains(userMatcher)) {
                    routesIterator.remove();
                }
            }
            return alertmanagerConfig;
        };
    }

    public static UnaryOperator<AlertmanagerConfig> getAddRouteFunc(
            AlertmanagerConfigSpec.ChildRoute route) {
        return alertmanagerConfig -> {

            if (!hasRoute(alertmanagerConfig, route)) {
                var configRoute = alertmanagerConfig.getSpec().getRoute() == null
                        ? new AlertmanagerConfigSpec.Route() : alertmanagerConfig.getSpec().getRoute();
                configRoute.getRoutes().add(route);
                alertmanagerConfig.getSpec().setRoute(configRoute);
            }

            return alertmanagerConfig;
        };
    }

    private static boolean hasRoute(AlertmanagerConfig alertmanagerConfig, AlertmanagerConfigSpec.ChildRoute route) {
        if (alertmanagerConfig.getSpec().getRoute() == null
                || alertmanagerConfig.getSpec().getRoute().getRoutes() == null) {
            return false;
        }
        return alertmanagerConfig.getSpec().getRoute().getRoutes().stream()
                .anyMatch(childRoute -> childRoute.equals(route));
    }
}
