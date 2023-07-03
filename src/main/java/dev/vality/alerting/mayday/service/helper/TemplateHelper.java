package dev.vality.alerting.mayday.service.helper;

import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.ParameterInfo;
import dev.vality.alerting.mayday.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.error.AlertConfigurationException;
import dev.vality.alerting.mayday.model.alerttemplate.AlertTemplate;
import dev.vality.alerting.mayday.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TemplateHelper {

    //TODO: do not pass from client empty params;
    private static final String emptyValue = "-";

    private final DictionaryService dictionaryService;

    public CreateAlertDto preparePrometheusRuleData(CreateAlertRequest createAlertRequest,
                                                    AlertTemplate metricTemplate,
                                                    List<AlertTemplate.AlertConfigurationParameter>
                                                            metricParams) {

        Map<String, String> parameters = mergeParameters(createAlertRequest.getParameters(), metricParams);
        String queryExpression = prepareMetricExpression(metricTemplate, parameters);
        String alertId = generateAlertId(createAlertRequest, queryExpression);

        //TODO: validation on required params? validate types correctness?
        //TODO: handle duration params
        return CreateAlertDto.builder()
                .alertId(alertId)
                .prometheusQuery(queryExpression)
                .userId(createAlertRequest.getUserId())
                .userFriendlyAlertName(prepareUserFriendlyAlertName(metricTemplate, parameters))
                .userFriendlyAlertDescription(prepareMetricAlertMessage(metricTemplate, parameters))
                .parameters(parameters)
                .formattedDurationMinutes(
                        formatDuration(parameters
                                .get(AlertConfigurationRequiredParameter.RULE_CHECK_DURATION_MINUTES
                                        .getSubstitutionName())))
                .build();
    }

    private Map<String, String> mergeParameters(List<ParameterInfo> externalParamsInfo,
                                                List<AlertTemplate.AlertConfigurationParameter>
                                                        maydayParamsInfo) {
        Map<String, String> params = maydayParamsInfo.stream()
                .map(maydayParamInfo -> {
                            var externalParamInfo = externalParamsInfo.stream()
                                    .filter(userParamInfo ->
                                            maydayParamInfo.getId().toString().equals(userParamInfo.getId()))
                                    .findFirst();

                            if (hasNoValue(externalParamInfo) && maydayParamInfo.getMandatory()) {
                                throw new AlertConfigurationException("Unable to find required" +
                                        " parameter: " + maydayParamInfo.getSubstitutionName());
                            }

                            String value = hasNoValue(externalParamInfo) ? ".*" : externalParamInfo.get().getValue();
                            if (!hasNoValue(externalParamInfo) && maydayParamInfo.getDictionaryName() != null) {
                                value = dictionaryService.getDictionary(maydayParamInfo.getDictionaryName()).get(value);
                            }
                            return new String[]{maydayParamInfo.getSubstitutionName(),
                                    value};
                        }
                )
                .collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));

        //Required parameters
        Arrays.stream(AlertConfigurationRequiredParameter.values()).forEach(
                requiredParameter -> {
                    var param = getRequiredParameter(requiredParameter.getSubstitutionName(), externalParamsInfo);
                    params.put(param.getId(), param.getValue());
                }
        );
        return params;
    }

    private ParameterInfo getRequiredParameter(String name, List<ParameterInfo> parameterInfos) {
        return parameterInfos.stream()
                .filter(paramInfo ->
                        paramInfo.getId().equals(name))
                .findFirst().orElseThrow(() -> new AlertConfigurationException("Unable to find required" +
                        " parameter: " + name));
    }

    private String generateAlertId(CreateAlertRequest createAlertRequest, String preparedExpression) {
        return DigestUtils.md5DigestAsHex((createAlertRequest.getUserId() + preparedExpression)
                .getBytes(StandardCharsets.UTF_8));
    }

    private String prepareMetricExpression(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getPrometheusQuery(), parameters);
    }

    private String prepareUserFriendlyAlertName(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertNameTemplate(), parameters);
    }

    private String prepareMetricAlertMessage(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertNotificationTemplate(), parameters);
    }

    private String prepareTemplate(String template, Map<String, String> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for (Map.Entry<String, String> entry : replacementsEntries) {
            preparedTemplate = preparedTemplate.replace(formatReplacementVariable(entry.getKey()), entry.getValue());
        }
        return preparedTemplate;
    }

    private String formatReplacementVariable(String variableName) {
        return "${" + variableName + "}";
    }

    private String formatDuration(String durationInMinutes) {
        return durationInMinutes + "m";
    }

    private boolean hasNoValue(Optional<ParameterInfo> parameterInfo) {
        return parameterInfo.isEmpty() || emptyValue.equals(parameterInfo.get().getValue());
    }
}
