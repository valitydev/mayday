package dev.vality.alerting.mayday.client;

import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfig;
import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfigSpec;
import dev.vality.alerting.mayday.client.util.K8sUtil;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static dev.vality.alerting.mayday.client.error.ErrorCodes.ALREADY_EXISTS_ERROR_CODE;

@Slf4j
@Component
@RequiredArgsConstructor
public class K8sAlertmanagerClient {

    private final Config k8sConfig;

    //TODO: check on startup?
    public Optional<AlertmanagerConfig> getAlertmanagerConfig(String alertmanagerConfigName)
            throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<AlertmanagerConfig, KubernetesResourceList<AlertmanagerConfig>, Resource<AlertmanagerConfig>>
                    alertmanagerConfigClient = client.resources(AlertmanagerConfig.class);
            var config = alertmanagerConfigClient.withName(alertmanagerConfigName).get();
            return Optional.ofNullable(config);
        }
    }

    public void createAlertmanagerConfig(AlertmanagerConfig alertmanagerConfig) {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<AlertmanagerConfig, KubernetesResourceList<AlertmanagerConfig>, Resource<AlertmanagerConfig>>
                    alertmanagerConfigClient = client.resources(AlertmanagerConfig.class);
            try {
                alertmanagerConfigClient.inNamespace(client.getNamespace()).resource(alertmanagerConfig).create();
            } catch (KubernetesClientException e) {
                if (!e.getStatus().getCode().equals(ALREADY_EXISTS_ERROR_CODE)) {
                    throw e;
                }
                log.warn("Tried to create already existing alertmanager config", e);
            }
        }
    }

    public void addReceiverAndRouteIfNotExists(String configName,
                                               AlertmanagerConfigSpec.ChildRoute route,
                                               AlertmanagerConfigSpec.Receiver receiver) {
        modifyAlertmanagerConfig(configName, K8sUtil.getAddReceiverAndRouteFunc(route, receiver));
    }

    private void modifyAlertmanagerConfig(String configName,
                                          UnaryOperator<AlertmanagerConfig> modifyFunc)
            throws KubernetesClientException {
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(k8sConfig).build()) {
            MixedOperation<AlertmanagerConfig, KubernetesResourceList<AlertmanagerConfig>, Resource<AlertmanagerConfig>>
                    alertmanagerConfigClient = client.resources(AlertmanagerConfig.class);
            var config =
                    alertmanagerConfigClient.inNamespace(client.getNamespace()).withName(configName).get();
            var resource = alertmanagerConfigClient.inNamespace(client.getNamespace()).resource(config);
            resource.edit(modifyFunc);
        }
    }

    public void deleteReceiver(String configName, String receiverName) {
        modifyAlertmanagerConfig(configName, K8sUtil.getRemoveReceiverByNameFunc(receiverName));
    }


}
