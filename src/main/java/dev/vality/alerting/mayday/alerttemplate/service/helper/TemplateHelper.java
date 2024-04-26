package dev.vality.alerting.mayday.alerttemplate.service.helper;

import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.ParameterInfo;
import dev.vality.alerting.mayday.alerttemplate.error.AlertConfigurationException;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.AlertTemplate;
import dev.vality.alerting.mayday.alerttemplate.model.dictionary.DictionaryData;
import dev.vality.alerting.mayday.alerttemplate.service.DictionaryService;
import dev.vality.alerting.mayday.common.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateHelper {

    private static final String anyClientValue = "-";
    private static final DictionaryData anyPrometheusValue = new DictionaryData(".*");
    private static final String anyUserFriendlyValue = "<любое значение>";
    private static final String multiValuePrometheusDelimiter = "|";
    private static final String multiValueUserFriendlyDelimiter = ",";

    private final DictionaryService dictionaryService;

    public CreateAlertDto preparePrometheusRuleData(CreateAlertRequest createAlertRequest,
                                                    AlertTemplate metricTemplate,
                                                    List<AlertTemplate.AlertConfigurationParameter>
                                                            metricParams) {

        var parameters = mergeParameters(createAlertRequest.getParameters(), metricParams);
        String queryExpression = prepareMetricExpression(metricTemplate, parameters);
        log.debug("Prepared prometheus expression: {}", queryExpression);
        String alertId = generateAlertId(createAlertRequest, queryExpression);

        return CreateAlertDto.builder()
                .alertId(alertId)
                .prometheusQuery(queryExpression)
                .userId(createAlertRequest.getUserId())
                .userFriendlyAlertName(prepareUserFriendlyAlertName(metricTemplate, parameters))
                .userFriendlyAlertDescription(prepareMetricAlertMessage(metricTemplate, parameters))
                .parameters(parameters)
                .build();
    }

    protected Map<String, List<DictionaryData>> mergeParameters(List<ParameterInfo> externalParamsInfo,
                                                                List<AlertTemplate.AlertConfigurationParameter>
                                                                        maydayParamsInfo) {
        Map<String, List<DictionaryData>> params = maydayParamsInfo.stream()
                .map(maydayParamInfo -> {
                            var externalParamInfos = externalParamsInfo.stream()
                                    .filter(userParamInfo ->
                                            maydayParamInfo.getId().toString().equals(userParamInfo.getId()))
                                    .toList();

                            validateMultipleValues(maydayParamInfo, externalParamInfos);
                            validateMandatoryValues(maydayParamInfo, externalParamInfos);

                            List<DictionaryData> values = new ArrayList<>();
                            if (!maydayParamInfo.getMandatory() && externalParamInfos.size() == 1) {
                                values.add(hasNoValue(externalParamInfos.get(0)) ? anyPrometheusValue :
                                        getParameterValue(maydayParamInfo, externalParamInfos.get(0)));
                            } else {
                                externalParamInfos.stream()
                                        .filter(parameterInfo -> !hasNoValue(parameterInfo))
                                        .map(parameterInfo -> getParameterValue(maydayParamInfo,
                                                parameterInfo))
                                        .forEach(values::add);
                            }
                            return Map.of(maydayParamInfo.getSubstitutionName(),
                                    values);
                        }
                ).flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // Add required parameters
        Arrays.stream(AlertConfigurationRequiredParameter.values()).forEach(
                requiredParameter -> {
                    var param = getRequiredParameter(String.valueOf(requiredParameter.getId()), externalParamsInfo);
                    params.put(requiredParameter.getSubstitutionName(),
                            Stream.of(param.getValue()).map(DictionaryData::new).toList()
                    );
                }
        );
        return params;
    }

    private void validateMultipleValues(AlertTemplate.AlertConfigurationParameter maydayParamInfo,
                                        List<ParameterInfo> externalParamInfos) {
        if (externalParamInfos.size() > 1 && !maydayParamInfo.getMultipleValues()) {
            throw new AlertConfigurationException(String.format("Parameter '%s' cannot have " +
                    "multiple values!", maydayParamInfo.getSubstitutionName()));
        }
    }

    private void validateMandatoryValues(AlertTemplate.AlertConfigurationParameter maydayParamInfo,
                                         List<ParameterInfo> externalParamInfos) {
        if ((externalParamInfos.isEmpty() || externalParamInfos.size() == 1
                && hasNoValue(externalParamInfos.get(0))) && maydayParamInfo.getMandatory()) {
            throw new AlertConfigurationException("Unable to find required" +
                    " parameter: " + maydayParamInfo.getSubstitutionName());
        }
    }

    protected ParameterInfo getRequiredParameter(String name, List<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .filter(paramInfo ->
                        paramInfo.getId().equals(name))
                .findFirst().orElseThrow(() -> new AlertConfigurationException("Unable to find required" +
                        " parameter: " + name));
    }

    protected String generateAlertId(CreateAlertRequest createAlertRequest, String preparedExpression) {
        return DigestUtils.md5DigestAsHex((createAlertRequest.getUserId() + preparedExpression)
                .getBytes(StandardCharsets.UTF_8));
    }

    protected String prepareMetricExpression(AlertTemplate metricTemplate,
                                             Map<String, List<DictionaryData>> parameters) {
        return prepareTemplate(metricTemplate.getPrometheusQuery(), parameters);
    }

    protected String prepareUserFriendlyAlertName(AlertTemplate metricTemplate,
                                                  Map<String, List<DictionaryData>> parameters) {
        return prepareUserFriendlyTemplate(metricTemplate.getAlertNameTemplate(), parameters);
    }

    protected String prepareMetricAlertMessage(AlertTemplate metricTemplate,
                                               Map<String, List<DictionaryData>> parameters) {
        return prepareUserFriendlyTemplate(metricTemplate.getAlertNotificationTemplate(), parameters);
    }

    private String prepareTemplate(String template, Map<String, List<DictionaryData>> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for (Map.Entry<String, List<DictionaryData>> entry : replacementsEntries) {
            var value = entry.getValue().stream()
                    .map(DictionaryData::getValue)
                    .collect(Collectors.joining(multiValuePrometheusDelimiter));
            preparedTemplate = preparedTemplate.replace(formatReplacementVariable(entry.getKey()), value);
        }
        return preparedTemplate;
    }

    private String prepareUserFriendlyTemplate(String template, Map<String, List<DictionaryData>> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for (Map.Entry<String, List<DictionaryData>> entry : replacementsEntries) {
            var value = entry.getValue().stream()
                    .map(DictionaryData::getUserFriendlyValue)
                    .map(this::formatAnyValue)
                    .collect(Collectors.joining(multiValueUserFriendlyDelimiter));
            preparedTemplate = preparedTemplate.replace(formatReplacementVariable(entry.getKey()), value);
        }
        return preparedTemplate;
    }

    private String formatAnyValue(String value) {
        if (anyPrometheusValue.getValue().equals(value)) {
            return anyUserFriendlyValue;
        }
        return value;
    }

    private DictionaryData getParameterValue(AlertTemplate.AlertConfigurationParameter maydayParamInfo,
                                             ParameterInfo userParamInfo) {
        if (maydayParamInfo.getDictionaryName() != null) {
            return dictionaryService.getDictionary(maydayParamInfo.getDictionaryName()).get(userParamInfo.getValue());
        }
        return new DictionaryData(userParamInfo.getValue());
    }

    private String formatReplacementVariable(String variableName) {
        return "${" + variableName + "}";
    }

    private String formatDuration(String durationInMinutes) {
        return durationInMinutes + "m";
    }

    private boolean hasNoValue(ParameterInfo parameterInfo) {
        return anyClientValue.equals(parameterInfo.getValue());
    }
}
