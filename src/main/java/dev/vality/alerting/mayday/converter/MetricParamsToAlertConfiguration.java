package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.ParameterConfiguration;
import dev.vality.alerting.mayday.ParameterType;
import dev.vality.alerting.mayday.domain.enums.MetricParamType;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MetricParamsToAlertConfiguration implements Converter<List<MetricParam>, AlertConfiguration> {
    @Override
    public AlertConfiguration convert(List<MetricParam> metricParams) {
        var alertConfiguration = new AlertConfiguration();
        alertConfiguration.setParameters(metricParams.stream().map(param -> new ParameterConfiguration()
                        .setId(Long.toString(param.getId()))
                        .setName(param.getDisplayName())
                        .setType(mapToParameterType(param.getParameterType())))
                .collect(Collectors.toList()));
        return alertConfiguration;
    }

    private ParameterType mapToParameterType(MetricParamType metricParamType) {
        return switch (metricParamType) {
            case bl -> ParameterType.bl;
            case fl -> ParameterType.fl;
            case str -> ParameterType.str;
            case integer -> ParameterType.integer;
        };
    }
}
