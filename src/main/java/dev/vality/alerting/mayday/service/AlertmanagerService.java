package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.client.K8sAlertmanagerClient;
import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfig;
import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.client.util.K8sUtil;
import dev.vality.alerting.mayday.config.properties.KubernetesProperties;
import dev.vality.alerting.mayday.config.properties.MaydayProperties;
import dev.vality.alerting.mayday.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.constant.K8sParameter;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.util.FormatUtil;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static dev.vality.alerting.mayday.constant.K8sParameter.ALERTMANAGER_CONFIG_NAME;

@Service
@RequiredArgsConstructor
public class AlertmanagerService {

    private static final String ONE_SEC_WAIT = FormatUtil.formatSecondsDuration("1");

    private final MaydayProperties maydayProperties;
    private final KubernetesProperties kubernetesProperties;
    private final K8sAlertmanagerClient k8sAlertmanagerClient;

    public void createUserRoute(CreateAlertDto createAlertDto) {
        if (k8sAlertmanagerClient.getAlertmanagerConfig(ALERTMANAGER_CONFIG_NAME).isEmpty()) {
            k8sAlertmanagerClient.createAlertmanagerConfig(buildAlertmanagerConfig());
        }

        AlertmanagerConfigSpec.ChildRoute route = new AlertmanagerConfigSpec.ChildRoute();
        route.setReceiver(K8sParameter.ALERTMANAGER_RECEIVER_NAME);
        route.setGroupBy(Set.of(PrometheusRuleAnnotation.ALERT_NAME));
        route.setGroupWait(ONE_SEC_WAIT);
        route.setGroupInterval(ONE_SEC_WAIT);
        var alertnameMatcher =
                K8sUtil.createMatcher(PrometheusRuleLabel.ALERTNAME_LABEL_NAME, createAlertDto.getAlertId());
        var usernameMatcher =
                K8sUtil.createMatcher(PrometheusRuleLabel.USERNAME_LABEL_NAME, createAlertDto.getUserId());
        route.setMatchers(Set.of(alertnameMatcher, usernameMatcher));
        route.setRepeatInterval(FormatUtil.formatMinutesDuration(createAlertDto.getParameters()
                .get(AlertConfigurationRequiredParameter.ALERT_REPEAT_MINUTES.getSubstitutionName()).get(0)));
        k8sAlertmanagerClient.addRouteIfNotExists(ALERTMANAGER_CONFIG_NAME, route);
    }

    public void deleteUserRoute(String alertId) {
        if (k8sAlertmanagerClient.getAlertmanagerConfig(ALERTMANAGER_CONFIG_NAME).isEmpty()) {
            return;
        }
        k8sAlertmanagerClient.deleteRoute(ALERTMANAGER_CONFIG_NAME, alertId);
    }

    public void deleteUserRoutes(String userId) {
        if (k8sAlertmanagerClient.getAlertmanagerConfig(ALERTMANAGER_CONFIG_NAME).isEmpty()) {
            return;
        }
        k8sAlertmanagerClient.deleteRoutes(ALERTMANAGER_CONFIG_NAME, userId);
    }

    private AlertmanagerConfig buildAlertmanagerConfig() {
        AlertmanagerConfigSpec.Route rootRoute = new AlertmanagerConfigSpec.Route();
        rootRoute.setReceiver(K8sParameter.ALERTMANAGER_RECEIVER_NAME);
        rootRoute.setMatchers(Set.of(K8sUtil.createMatcher("service", "mayday")));
        AlertmanagerConfigSpec.Receiver receiver = new AlertmanagerConfigSpec.Receiver();
        receiver.setName(K8sParameter.ALERTMANAGER_RECEIVER_NAME);
        var webhookConfig = new AlertmanagerConfigSpec.WebhookConfig();
        webhookConfig.setUrl(maydayProperties.getWebhookUrl() + maydayProperties.getAlertmanagerWebhookPath());
        receiver.setWebhookConfigs(Set.of(webhookConfig));
        AlertmanagerConfigSpec spec = new AlertmanagerConfigSpec();
        spec.setReceivers(Set.of(receiver));
        spec.setRoute(rootRoute);
        AlertmanagerConfig alertmanagerConfig = new AlertmanagerConfig();
        alertmanagerConfig.setSpec(spec);
        var metadata = new ObjectMeta();
        metadata.setLabels(kubernetesProperties.getAlertmanagerConfiguration().getLabels());
        metadata.setName(ALERTMANAGER_CONFIG_NAME);
        alertmanagerConfig.setMetadata(metadata);
        return alertmanagerConfig;
    }
}
