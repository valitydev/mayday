package dev.vality.alerting.mayday.integration;

import dev.vality.alerting.mayday.Alert;
import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.AlertingServiceSrv;
import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.alertmanager.client.k8s.AlertmanagerClient;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.service.AlertmanagerService;
import dev.vality.alerting.mayday.alerttemplate.dao.DawayDao;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.prometheus.client.k8s.PrometheusClient;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRule;
import dev.vality.alerting.mayday.prometheus.service.PrometheusService;
import dev.vality.alerting.mayday.testutil.DawayObjectUtil;
import dev.vality.alerting.mayday.testutil.K8sObjectUtil;
import dev.vality.alerting.mayday.testutil.ThriftObjectUtil;
import dev.vality.testcontainers.annotations.DefaultSpringBootTest;
import org.apache.thrift.TException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    @Autowired
    private PrometheusService prometheusService;
    @Autowired
    private AlertmanagerService alertmanagerService;

    @MockBean
    private PrometheusClient prometheusClient;
    @MockBean
    private AlertmanagerClient alertmanagerClient;
    @MockBean
    private DawayDao dawayDao;

    private AutoCloseable mocks;

    private Object[] preparedMocks;

    @BeforeEach
    public void init() {
        mocks = MockitoAnnotations.openMocks(this);
        preparedMocks = new Object[]{prometheusClient, alertmanagerClient, dawayDao};
    }

    @AfterEach
    public void clean() throws Exception {
        verifyNoMoreInteractions(preparedMocks);
        mocks.close();
    }

    @Test
    void createAlert() throws TException {
        when(prometheusClient.getPrometheusRule(prometheusService.getPrometheusRuleName()))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(alertmanagerClient.getAlertmanagerConfig(alertmanagerService.getAlertmanagerConfigName()))
                .thenReturn(Optional.of(new AlertmanagerConfig()));
        when(dawayDao.getPaymentProviders()).thenReturn(DawayObjectUtil.getTestProviders());
        when(dawayDao.getPaymentTerminals()).thenReturn(DawayObjectUtil.getTestTerminals());
        when(dawayDao.getShops()).thenReturn(DawayObjectUtil.getTestShops());
        when(dawayDao.getCurrencies()).thenReturn(DawayObjectUtil.getTestCurrencies());

        var createAlertRequest =
                ThriftObjectUtil.testCreatePaymentConversionAlertRequest(getPaymentConversionAlertConfiguration());
        thriftEndpoint.createAlert(createAlertRequest);
        verify(prometheusClient, times(1)).getPrometheusRule(prometheusService.getPrometheusRuleName());
        verify(prometheusClient, times(1))
                .addAlertToPrometheusRuleGroup(eq(prometheusService.getPrometheusRuleName()),
                eq(createAlertRequest.getUserId()), any());
        verify(alertmanagerClient, times(1))
                .getAlertmanagerConfig(eq(alertmanagerService.getAlertmanagerConfigName()));
        verify(alertmanagerClient, times(1))
                .addRouteIfNotExists(eq(alertmanagerService.getAlertmanagerConfigName()), any());

        verify(dawayDao, times(2)).getPaymentProviders();
        verify(dawayDao, times(2)).getPaymentTerminals();
        verify(dawayDao, times(2)).getShops();
        verify(dawayDao, times(2)).getCurrencies();
    }

    @Test
    void getUserAlertsEmpty() throws TException {
        String userName = UUID.randomUUID().toString();
        when(prometheusClient.getPrometheusRule(prometheusService.getPrometheusRuleName()))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(prometheusClient.getPrometheusRuleGroupAlerts(prometheusService.getPrometheusRuleName(), userName))
                .thenReturn(Set.of());
        List<UserAlert> userAlerts = thriftEndpoint.getUserAlerts(userName);
        assertNotNull(userAlerts);
        assertTrue(userAlerts.isEmpty());
        verify(prometheusClient, times(1))
                .getPrometheusRule(prometheusService.getPrometheusRuleName());
        verify(prometheusClient, times(1))
                .getPrometheusRuleGroupAlerts(prometheusService.getPrometheusRuleName(), userName);
    }

    @Test
    void getUserAlerts() throws TException {
        String userName = UUID.randomUUID().toString();
        var testRule = K8sObjectUtil.testPrometheusRule();
        when(prometheusClient.getPrometheusRule(prometheusService.getPrometheusRuleName()))
                .thenReturn(Optional.of(new PrometheusRule()));
        when(prometheusClient.getPrometheusRuleGroupAlerts(prometheusService.getPrometheusRuleName(), userName))
                .thenReturn(Set.of(testRule));
        List<UserAlert> userAlerts = thriftEndpoint.getUserAlerts(userName);
        assertNotNull(userAlerts);
        assertEquals(1, userAlerts.size());

        UserAlert userAlert = userAlerts.get(0);
        assertEquals(testRule.getAlert(), userAlert.getId());
        assertEquals(testRule.getAnnotations().get(PrometheusRuleAnnotation.ALERT_NAME), userAlert.getName());
        verify(prometheusClient, times(1))
                .getPrometheusRule(prometheusService.getPrometheusRuleName());
        verify(prometheusClient, times(1))
                .getPrometheusRuleGroupAlerts(prometheusService.getPrometheusRuleName(), userName);
    }

    AlertConfiguration getPaymentConversionAlertConfiguration() throws TException {
        List<Alert> alertList = getSupportedAlerts();
        AlertConfiguration alertConfiguration =
                thriftEndpoint.getAlertConfiguration(alertList.stream()
                        .filter(alert -> alert.getId().equals("payment_conversion"))
                        .findFirst()
                        .orElseThrow().getId());
        assertNotNull(alertConfiguration);
        assertNotNull(alertConfiguration.getId());
        assertNotNull(alertConfiguration.getParameters());
        assertFalse(alertConfiguration.getParameters().isEmpty());
        return alertConfiguration;
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
