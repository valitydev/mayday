package dev.vality.alerting.mayday.alertmanager.client.k8s;

import dev.vality.alerting.mayday.alertmanager.client.k8s.util.AlertmanagerFunctionsUtil;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfigSpec;
import io.fabric8.kubernetes.api.model.KubernetesResourceList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.UnaryOperator;


@Slf4j
@Component
@RequiredArgsConstructor
public class AlertmanagerClient {

    private final Config k8sConfig;

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
                // 409 http код возникает при попытке создать уже существующий объект
                if (!e.getStatus().getCode().equals(HttpStatus.CONFLICT.value())) {
                    throw e;
                }
                log.warn("Tried to create already existing alertmanager config", e);
            }
        }
    }

    public void addRouteIfNotExists(String configName,
                                    AlertmanagerConfigSpec.ChildRoute route) {
        modifyAlertmanagerConfig(configName, AlertmanagerFunctionsUtil.getAddRouteFunc(route));
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

    public void deleteRoute(String configName, String alertId) {
        modifyAlertmanagerConfig(configName, AlertmanagerFunctionsUtil.getRemoveRouteByAlertIdFunc(alertId));
    }

    public void deleteRoutes(String configName, String userId) {
        modifyAlertmanagerConfig(configName, AlertmanagerFunctionsUtil.getRemoveUserRoutesFunc(userId));
    }


}
