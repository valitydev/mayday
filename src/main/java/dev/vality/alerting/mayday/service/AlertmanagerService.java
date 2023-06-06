package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.client.K8sAlertmanagerClient;
import dev.vality.alerting.mayday.client.K8sPrometheusClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlertmanagerService {

    private final K8sAlertmanagerClient k8sAlertmanagerClient;

    public void createUserRoute(String userId, Object routeInfo) {

    }

    public void deleteAllUserRoutes(String userId) {

    }

    public void deleteUserRoute(String userId, String alertId) {

    }
}
