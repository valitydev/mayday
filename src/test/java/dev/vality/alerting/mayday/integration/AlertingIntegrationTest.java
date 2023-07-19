package dev.vality.alerting.mayday.integration;

import dev.vality.alerting.mayday.Alert;
import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.AlertingServiceSrv;
import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.alertmanager.client.k8s.AlertmanagerClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.constant.K8sParameter;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.testutil.K8sObjectUtil;
import dev.vality.alerting.mayday.testutil.ThriftObjectUtil;
import dev.vality.testcontainers.annotations.DefaultSpringBootTest;
import org.apache.thrift.TException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DefaultSpringBootTest
public class AlertingIntegrationTest {

    @Autowired
    private AlertingServiceSrv.Iface thriftEndpoint;

    @MockBean
    private PrometheusClient prometheusClient;
    @MockBean
    private AlertmanagerClient alertmanagerClient;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{prometheusClient, alertmanagerClient};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }


    AlertConfiguration getAlertConfiguration() throws TException {
        List<Alert> alertList = getSupportedAlerts();
        AlertConfiguration alertConfiguration = thriftEndpoint.getAlertConfiguration(alertList.get(0).getId());
        assertNotNull(alertConfiguration);
        assertNotNull(alertConfiguration.getId());
        assertNotNull(alertConfiguration.getParameters());
        assertFalse(alertConfiguration.getParameters().isEmpty());
        return alertConfiguration;
    }

    @Test
    void getUserAlertsEmpty() throws TException {
        String userName = UUID.randomUUID().toString();
        when(prometheusClient.getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(prometheusClient.getPrometheusRuleGroupAlerts(K8sParameter.PROMETHEUS_RULE_NAME, userName))
                .thenReturn(Set.of());
        List<UserAlert> userAlerts = thriftEndpoint.getUserAlerts(userName);
        assertNotNull(userAlerts);
        assertTrue(userAlerts.isEmpty());
        verify(prometheusClient, times(1))
                .getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME);
        verify(prometheusClient, times(1))
                .getPrometheusRuleGroupAlerts(K8sParameter.PROMETHEUS_RULE_NAME, userName);
    }

    @Test
    void getUserAlerts() throws TException {
        String userName = UUID.randomUUID().toString();
        var testRule = K8sObjectUtil.testPrometheusRule();
        when(prometheusClient.getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(prometheusClient.getPrometheusRuleGroupAlerts(K8sParameter.PROMETHEUS_RULE_NAME, userName)).thenReturn(
                Set.of(testRule)
        );
        List<UserAlert> userAlerts = thriftEndpoint.getUserAlerts(userName);
        assertNotNull(userAlerts);
        assertEquals(1, userAlerts.size());

        UserAlert userAlert = userAlerts.get(0);
        assertEquals(testRule.getAlert(), userAlert.getId());
        assertEquals(testRule.getAnnotations().get(PrometheusRuleAnnotation.ALERT_NAME), userAlert.getName());
        verify(prometheusClient, times(1))
                .getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME);
        verify(prometheusClient, times(1))
                .getPrometheusRuleGroupAlerts(K8sParameter.PROMETHEUS_RULE_NAME, userName);
    }

    @Test
    @Disabled
    void createAlert() throws TException {
        var createAlertRequest = ThriftObjectUtil.testCreateAlertRequest(getAlertConfiguration());
        when(prometheusClient.getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(alertmanagerClient.getAlertmanagerConfig(K8sParameter.ALERTMANAGER_CONFIG_NAME))
                .thenReturn(Optional.of(new AlertmanagerConfig()));
        thriftEndpoint.createAlert(createAlertRequest);
        verify(prometheusClient, times(1)).getPrometheusRule(K8sParameter.PROMETHEUS_RULE_NAME);
        verify(prometheusClient, times(1))
                .addAlertToPrometheusRuleGroup(eq(K8sParameter.PROMETHEUS_RULE_NAME),
                eq(createAlertRequest.getUserId()), any());
        verify(alertmanagerClient, times(1))
                .getAlertmanagerConfig(eq(K8sParameter.ALERTMANAGER_CONFIG_NAME));
        verify(alertmanagerClient, times(1))
                .addRouteIfNotExists(eq(K8sParameter.ALERTMANAGER_CONFIG_NAME), any());
    }

    private List<Alert> getSupportedAlerts() throws TException {
        List<Alert> alertList = thriftEndpoint.getSupportedAlerts();
        assertNotNull(alertList);
        assertFalse(alertList.isEmpty());

        for (Alert alert : alertList) {
            assertNotNull(alert.getId());
            assertNotNull(alert.getName());
        }
        return alertList;
    }
}
