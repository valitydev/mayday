package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.ParameterInfo;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class ThriftObjectUtil {

    public static CreateAlertRequest testCreatePaymentConversionAlertRequest(AlertConfiguration alertConfiguration) {
        var request = new CreateAlertRequest();
        request.setAlertId(alertConfiguration.getId());
        request.setUserId(UUID.randomUUID().toString());
        List<ParameterInfo> parameters = new ArrayList<>();

        var providerParameter = new ParameterInfo();
        providerParameter.setId("1");
        providerParameter.setValue("(1) provider");
        parameters.add(providerParameter);

        var terminalParameter = new ParameterInfo();
        terminalParameter.setId("2");
        terminalParameter.setValue("(1) terminal");
        parameters.add(terminalParameter);

        var shopParameter = new ParameterInfo();
        shopParameter.setId("3");
        shopParameter.setValue("(def91399) shop");
        parameters.add(shopParameter);

        var currencyParameter = new ParameterInfo();
        currencyParameter.setId("4");
        currencyParameter.setValue("(RUB) Рублик");
        parameters.add(currencyParameter);

        var boundaryParameter = new ParameterInfo();
        boundaryParameter.setId("5");
        boundaryParameter.setValue("Больше порогового значения");
        parameters.add(boundaryParameter);

        var thresholdParameter = new ParameterInfo();
        thresholdParameter.setId("6");
        thresholdParameter.setValue("10");
        parameters.add(thresholdParameter);

        var periodParameter = new ParameterInfo();
        periodParameter.setId("7");
        periodParameter.setValue("15 минут");
        parameters.add(periodParameter);

        var ruleCheckDurationParameter = new ParameterInfo();
        ruleCheckDurationParameter.setId(String.valueOf(Integer.MAX_VALUE));
        ruleCheckDurationParameter.setValue("10");
        parameters.add(ruleCheckDurationParameter);

        var alertRepeatParameter = new ParameterInfo();
        alertRepeatParameter.setId(String.valueOf(Integer.MAX_VALUE - 1));
        alertRepeatParameter.setValue("10");
        parameters.add(alertRepeatParameter);

        request.setParameters(parameters);
        return request;
    }
}
