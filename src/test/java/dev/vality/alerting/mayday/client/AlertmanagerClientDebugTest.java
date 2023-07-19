package dev.vality.alerting.mayday.client;

import dev.vality.alerting.mayday.alertmanager.client.k8s.AlertmanagerClient;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.constant.K8sParameter;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;


@Disabled("For local client testing")
class AlertmanagerClientDebugTest {

    private static final String alertmanagerConfigName = "testconfig";
    private static final String receiverName = "test_receiver";
    private static final String webhookUrl = "https://webhook.site/e27da8da-2f80-4ecd-b494-fbe15c84b70f";
    private final Config config = new ConfigBuilder().withDefaultNamespace().build();
    private final AlertmanagerClient client = new AlertmanagerClient(config);

    @Test
    void getAlertmanagerConfig() {
        var response = client.getAlertmanagerConfig(alertmanagerConfigName);
        System.out.println(response);
    }

    @Test
    void deleteAlertmanagerConfig() {
        //client.deleteAlertmanagerConfig(alertmanagerConfigName);
    }

    @Test
    void createAlertmanagerConfig() {
        var metadata = new ObjectMeta();
        metadata.setLabels(Map.of("alerting-stage", "test"));
        metadata.setName(alertmanagerConfigName);
        metadata.setLabels(
                Map.of("service", "mayday")
        );
        AlertmanagerConfig alertmanagerConfig = new AlertmanagerConfig();
        alertmanagerConfig.setMetadata(metadata);
        AlertmanagerConfigSpec.Receiver receiver = new AlertmanagerConfigSpec.Receiver();
        AlertmanagerConfigSpec.WebhookConfig webhookConfig = new AlertmanagerConfigSpec.WebhookConfig();
        webhookConfig.setUrl(webhookUrl);
        receiver.setName(K8sParameter.ALERTMANAGER_CONFIG_NAME);
        receiver.setWebhookConfigs(Set.of(webhookConfig));
        AlertmanagerConfigSpec alertmanagerConfigSpec = new AlertmanagerConfigSpec();
        alertmanagerConfigSpec.setReceivers(Set.of(receiver));
        alertmanagerConfig.setSpec(alertmanagerConfigSpec);
        alertmanagerConfigSpec.setRoute(new AlertmanagerConfigSpec.Route());
        AlertmanagerConfigSpec.ChildRoute route = new AlertmanagerConfigSpec.ChildRoute();
        route.setReceiver(K8sParameter.ALERTMANAGER_CONFIG_NAME);
        route.setGroupBy(Set.of("'...'"));
        alertmanagerConfigSpec.getRoute().setRoutes(Set.of(route));
        client.createAlertmanagerConfig(alertmanagerConfig);
    }

    @Test
    void addReceiverIfNotExists() {
        AlertmanagerConfigSpec.ChildRoute route = new AlertmanagerConfigSpec.ChildRoute();
        route.setReceiver(receiverName);
        route.setRepeatInterval("5m");

        AlertmanagerConfigSpec.Receiver receiver = new AlertmanagerConfigSpec.Receiver();
        client.addRouteIfNotExists(alertmanagerConfigName, route);
    }

    @Test
    void deleteReceivers() {
        client.deleteRoutes(alertmanagerConfigName, receiverName);
    }
}