package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.AlertConfiguration;
import dev.vality.alerting.mayday.ParameterConfiguration;
import dev.vality.alerting.mayday.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.model.alerttemplate.AlertTemplate;
import dev.vality.alerting.mayday.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlertParamsToAlertConfiguration implements Converter<List<AlertTemplate.AlertConfigurationParameter>,
        AlertConfiguration> {

    private final DictionaryService dictionaryService;

    @Override
    public AlertConfiguration convert(List<AlertTemplate.AlertConfigurationParameter> alertParams) {
        var alertConfiguration = new AlertConfiguration();
        alertConfiguration.setParameters(alertParams.stream().map(param -> new ParameterConfiguration()
                        .setId(param.getId().toString())
                        .setName(param.getReadableName())
                        .setMandatory(param.getMandatory())
                        .setValueRegexp(param.getRegexp())
                        .setOptions(param.getDictionaryName() != null ? dictionaryService
                                .getDictionary(param.getDictionaryName()).keySet().stream().toList()
                                : null))
                .collect(Collectors.toList()));
        alertConfiguration.getParameters().addAll(Arrays.stream(AlertConfigurationRequiredParameter.values())
                .map(requiredParameter ->
                        new ParameterConfiguration()
                                .setId(requiredParameter.getSubstitutionName())
                                .setName(requiredParameter.getReadableName())
                                .setMandatory(true)
                                .setValueRegexp(requiredParameter.getRegexp())
        ).toList());
        return alertConfiguration;
    }
}
