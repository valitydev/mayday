package dev.vality.alerting.mayday.unit;

import dev.vality.alerting.mayday.Alert;
import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.AlertingServiceSrv;
import dev.vality.alerting.mayday.alertmanager.client.k8s.AlertmanagerClient;
import dev.vality.alerting.mayday.alertmanager.client.k8s.model.AlertmanagerConfig;
import dev.vality.alerting.mayday.alertmanager.service.AlertmanagerService;
import dev.vality.alerting.mayday.alerttemplate.dao.DawayDao;
import dev.vality.alerting.mayday.alerttemplate.service.TemplateService;
import dev.vality.alerting.mayday.alerttemplate.service.helper.TemplateHelper;
import dev.vality.alerting.mayday.testutil.DawayObjectUtil;
import dev.vality.alerting.mayday.testutil.ThriftObjectUtil;
import dev.vality.testcontainers.annotations.DefaultSpringBootTest;
import org.apache.thrift.TException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@DefaultSpringBootTest
public class TemplateHelperTest {

    @Autowired
    private AlertingServiceSrv.Iface thriftEndpoint;

    @Autowired
    private AlertmanagerService alertmanagerService;

    @Autowired
    private TemplateHelper templateHelper;

    @Autowired
    private TemplateService templateService;

    @MockBean
    private AlertmanagerClient alertmanagerClient;
    @MockBean
    private DawayDao dawayDao;


    private static final String prometheusQuery = "round(100 * sum(ebm_payments_status_count{provider_id=~\"1\", " +
            "terminal_id=~\"1\",shop_id=~\"def91399-75ff-4307-8634-626c85859ea4\",currency=~\"RUB\",duration=\"15m\"," +
            "status=\"captured\"}) / sum(ebm_payments_status_count{provider_id=~\"1\",terminal_id=~\"1\"," +
            "shop_id=~\"def91399-75ff-4307-8634-626c85859ea4\",currency=~\"RUB\",duration=\"15m\"}), 1) > 10";

    private static final String userFriendlyAlertName = "Конверсия платежей по провайдеру 'provider (1)', " +
            "терминалу 'terminal (1)', валюте 'RUB' и магазину 'shop (def91399-75ff-4307-8634-626c85859ea4)' " +
            "за период: 15m > 10%";

    @Test
    void preparePrometheusRuleDataTest() throws TException {

        when(alertmanagerClient.getAlertmanagerConfig(alertmanagerService.getAlertmanagerConfigName()))
                .thenReturn(Optional.of(new AlertmanagerConfig()));
        when(dawayDao.getPaymentProviders()).thenReturn(DawayObjectUtil.getTestProviders());
        when(dawayDao.getPaymentTerminals()).thenReturn(DawayObjectUtil.getTestTerminals());
        when(dawayDao.getShops()).thenReturn(DawayObjectUtil.getTestShops());
        when(dawayDao.getCurrencies()).thenReturn(DawayObjectUtil.getTestCurrencies());

        var createAlertRequest =
                ThriftObjectUtil.testCreatePaymentConversionAlertRequest(getPaymentConversionAlertConfiguration());

        var metricParams = templateService.getAlertTemplateParams(createAlertRequest.getAlertId());
        var metricTemplate = templateService.getAlertTemplateById(createAlertRequest.getAlertId());
        var result = templateHelper.preparePrometheusRuleData(createAlertRequest, metricTemplate, metricParams);

        assertNotNull(result);
        assertEquals(prometheusQuery, result.getPrometheusQuery());
        assertEquals(userFriendlyAlertName, result.getUserFriendlyAlertName());
    }


    AlertConfiguration getPaymentConversionAlertConfiguration() throws TException {
        List<Alert> alertList = thriftEndpoint.getSupportedAlerts();
        return
                thriftEndpoint.getAlertConfiguration(alertList.stream()
                        .filter(alert -> alert.getId().equals("payment_conversion"))
                        .findFirst()
                        .orElseThrow().getId());
    }
}
