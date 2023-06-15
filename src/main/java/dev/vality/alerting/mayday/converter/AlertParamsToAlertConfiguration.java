package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.ParameterConfiguration;
import dev.vality.alerting.mayday.ParameterType;
import dev.vality.alerting.mayday.constant.MetricRequiredParameter;
import dev.vality.alerting.mayday.domain.enums.AlertParamType;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AlertParamsToAlertConfiguration implements Converter<List<AlertParam>, AlertConfiguration> {
    @Override
    public AlertConfiguration convert(List<AlertParam> alertParams) {
        var alertConfiguration = new AlertConfiguration();
        alertConfiguration.setParameters(alertParams.stream().map(param -> new ParameterConfiguration()
                        .setId(param.getId().toString())
                        .setName(param.getDisplayName())
                        .setType(mapToParameterType(param.getParameterType())))
                .collect(Collectors.toList()));

        alertConfiguration.getParameters().addAll(Arrays.stream(MetricRequiredParameter.values()).map(metricRequiredParameter -> {
            var paramConfiguration = new ParameterConfiguration();
            paramConfiguration.setType(ParameterType.str);
            paramConfiguration.setId(metricRequiredParameter.getParameterName());
            paramConfiguration.setName(metricRequiredParameter.getParameterTemplate());
            return paramConfiguration;
        }).toList());
        return alertConfiguration;
    }

    private ParameterType mapToParameterType(AlertParamType alertParamType) {
        return switch (alertParamType) {
            case bl -> ParameterType.bl;
            case fl -> ParameterType.fl;
            case str -> ParameterType.str;
            case integer -> ParameterType.integer;
        };
    }
}
