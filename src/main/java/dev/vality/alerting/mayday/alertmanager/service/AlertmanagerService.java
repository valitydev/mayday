package dev.vality.alerting.mayday.alertmanager.service;

import dev.vality.alerting.mayday.alertmanager.client.k8s.AlertmanagerClient;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.alertmanager.client.k8s.util.AlertmanagerFunctionsUtil;
import dev.vality.alerting.mayday.alertmanager.config.properties.AlertmanagerWebhookProperties;
import dev.vality.alerting.mayday.alertmanager.config.properties.K8sAlertmanagerProperties;
import dev.vality.alerting.mayday.alertmanager.util.FormatUtil;
import dev.vality.alerting.mayday.common.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Set;


@Slf4j
@Service
@RequiredArgsConstructor
public class AlertmanagerService {

    private static final String ONE_SEC_WAIT = FormatUtil.formatSecondsDuration("1");

    private final AlertmanagerWebhookProperties alertmanagerWebhookProperties;
    private final K8sAlertmanagerProperties k8sAlertmanagerProperties;
    private final AlertmanagerClient alertmanagerClient;
    @Value("${spring.application.name}")
    private String applicationName;
    private String alertmanagerConfigName;

    public void createUserRoute(CreateAlertDto createAlertDto) {
        if (alertmanagerClient.getAlertmanagerConfig(getAlertmanagerConfigName()).isEmpty()) {
            log.info("Alertmanager config '{}' not found and will be created", getAlertmanagerConfigName());
            alertmanagerClient.createAlertmanagerConfig(buildAlertmanagerConfig());
            log.info("Alertmanager config '{}' was created successfully", getAlertmanagerConfigName());
        }
        alertmanagerClient.addRouteIfNotExists(getAlertmanagerConfigName(), buildRoute(createAlertDto));
    }

    public String getAlertmanagerConfigName() {
        if (ObjectUtils.isEmpty(alertmanagerConfigName)) {
            alertmanagerConfigName = "%s-managed-rule".formatted(applicationName);
        }
        return alertmanagerConfigName;
    }

    private AlertmanagerConfig buildAlertmanagerConfig() {
        var webhookConfig = buildWebhookConfig();
        var receiver = buildAlertManagerConfigReceiver(webhookConfig);
        var rootRoute = buildAlertmanagerConfigRootRoute();
        var spec = buildAlertmanagerConfigSpec(receiver, rootRoute);
        AlertmanagerConfig alertmanagerConfig = new AlertmanagerConfig();
        alertmanagerConfig.setSpec(spec);
        alertmanagerConfig.setMetadata(buildAlertmanagerConfigMetadata());
        return alertmanagerConfig;
    }

    private AlertmanagerConfigSpec.ChildRoute buildRoute(CreateAlertDto createAlertDto) {
        AlertmanagerConfigSpec.ChildRoute route = new AlertmanagerConfigSpec.ChildRoute();
        route.setReceiver(applicationName);
        route.setGroupBy(Set.of(PrometheusRuleLabel.ALERT_NAME));
        route.setGroupWait(ONE_SEC_WAIT);
        route.setGroupInterval(ONE_SEC_WAIT);
        var alertnameMatcher =
                AlertmanagerFunctionsUtil.createMatcher(PrometheusRuleLabel.ALERT_NAME, createAlertDto.getAlertId());
        var usernameMatcher =
                AlertmanagerFunctionsUtil.createMatcher(PrometheusRuleLabel.USERNAME, createAlertDto.getUserId());
        route.setMatchers(Set.of(alertnameMatcher, usernameMatcher));
        route.setRepeatInterval(FormatUtil.formatMinutesDuration(createAlertDto.getParameters()
                .get(String.valueOf(
                        AlertConfigurationRequiredParameter.ALERT_REPEAT_MINUTES.getSubstitutionName())).get(0)));
        return route;
    }

    private AlertmanagerConfigSpec.WebhookConfig buildWebhookConfig() {
        var webhookConfig = new AlertmanagerConfigSpec.WebhookConfig();
        webhookConfig.setUrl(alertmanagerWebhookProperties.getUrl() + alertmanagerWebhookProperties.getPath());
        return webhookConfig;
    }

    private AlertmanagerConfigSpec.Receiver buildAlertManagerConfigReceiver(
            AlertmanagerConfigSpec.WebhookConfig webhookConfig) {
        AlertmanagerConfigSpec.Receiver receiver = new AlertmanagerConfigSpec.Receiver();
        receiver.setName(applicationName);
        receiver.setWebhookConfigs(Set.of(webhookConfig));
        return receiver;
    }

    private AlertmanagerConfigSpec.Route buildAlertmanagerConfigRootRoute() {
        AlertmanagerConfigSpec.Route rootRoute = new AlertmanagerConfigSpec.Route();
        rootRoute.setReceiver(applicationName);
        rootRoute.setMatchers(Set.of(AlertmanagerFunctionsUtil.createMatcher(PrometheusRuleLabel.SERVICE,
                applicationName)));
        return rootRoute;
    }

    private AlertmanagerConfigSpec buildAlertmanagerConfigSpec(AlertmanagerConfigSpec.Receiver receiver,
                                                               AlertmanagerConfigSpec.Route route) {
        AlertmanagerConfigSpec spec = new AlertmanagerConfigSpec();
        spec.setReceivers(Set.of(receiver));
        spec.setRoute(route);
        return spec;
    }

    private ObjectMeta buildAlertmanagerConfigMetadata() {
        var metadata = new ObjectMeta();
        metadata.setLabels(k8sAlertmanagerProperties.getLabels());
        metadata.setName(getAlertmanagerConfigName());
        return metadata;
    }

    public void deleteUserRoute(String alertId) {
        if (alertmanagerClient.getAlertmanagerConfig(getAlertmanagerConfigName()).isEmpty()) {
            log.warn("Alertmanager config '{}' not found, no need to delete user route", getAlertmanagerConfigName());
            return;
        }
        alertmanagerClient.deleteRoute(getAlertmanagerConfigName(), alertId);
    }

    public void deleteUserRoutes(String userId) {
        if (alertmanagerClient.getAlertmanagerConfig(getAlertmanagerConfigName()).isEmpty()) {
            log.warn("Alertmanager config '{}' not found, no need to delete user route", getAlertmanagerConfigName());
            return;
        }
        alertmanagerClient.deleteRoutes(getAlertmanagerConfigName(), userId);
    }

    public boolean containsUserRoute(String userId, String alertName) {
        return alertmanagerClient.containsRoute(getAlertmanagerConfigName(), userId, alertName);
    }
}
