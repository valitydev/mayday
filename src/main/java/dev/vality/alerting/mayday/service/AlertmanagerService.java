package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.client.K8sAlertmanagerClient;
import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.constant.K8sParameter;
import dev.vality.alerting.mayday.constant.MetricRequiredParameter;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.util.RedirectUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlertmanagerService {

    private static final String DO_NOT_WAIT = "0s";

    private final K8sAlertmanagerClient k8sAlertmanagerClient;

    public void createUserRoute(CreateAlertDto createAlertDto) {
        AlertmanagerConfigSpec.Receiver receiver = new AlertmanagerConfigSpec.Receiver();
        receiver.setName(K8sParameter.ALERTMANAGER_RECEIVER_NAME);
        var webhookConfig = new AlertmanagerConfigSpec.WebhookConfig();
        webhookConfig.setUrl(RedirectUtil.getAlertmanagerWebhookUrl());
        receiver.setWebhookConfigs(Set.of(webhookConfig));

        AlertmanagerConfigSpec.ChildRoute route = new AlertmanagerConfigSpec.ChildRoute();
        route.setReceiver(receiver.getName());
        route.setGroupBy(Set.of(PrometheusRuleAnnotation.ALERT_NAME, PrometheusRuleAnnotation.USERNAME));
        route.setGroupWait(DO_NOT_WAIT);
        route.setGroupInterval(DO_NOT_WAIT);
        //TODO: FormatUtil?
        route.setRepeatInterval(createAlertDto.getParameters()
                .get(MetricRequiredParameter.ALERT_DURATION_MINUTES.getParameterName()) + "m");
        k8sAlertmanagerClient.addReceiverAndRouteIfNotExists(K8sParameter.ALERTMANAGER_CONFIG_NAME, route, receiver);
    }

    public void deleteUserRoute(String userId, String alertId) {
        k8sAlertmanagerClient.
    }

    public void deleteUserRoutes(String userId) {

    }
}
