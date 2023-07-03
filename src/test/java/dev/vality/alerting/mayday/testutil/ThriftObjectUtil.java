package dev.vality.alerting.mayday.testutil;

import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.ParameterInfo;
import lombok.experimental.UtilityClass;

import java.util.UUID;
import java.util.stream.Collectors;

@UtilityClass
public class ThriftObjectUtil {

    public static CreateAlertRequest testCreateAlertRequest(AlertConfiguration alertConfiguration) {
        var request = new CreateAlertRequest();
        request.setAlertId(alertConfiguration.getId());
        request.setUserId(UUID.randomUUID().toString());
        request.setParameters(
                alertConfiguration.getParameters().stream().map(parameterConfiguration -> {
                    var paramInfo = new ParameterInfo();
                    paramInfo.setId(parameterConfiguration.getId());
                    paramInfo.setValue(UUID.randomUUID().toString());
                    return paramInfo;
                }).collect(Collectors.toList()));
        return request;
    }
}
