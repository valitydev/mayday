package dev.vality.alerting.mayday.service.helper;

import dev.vality.alerting.mayday.ParameterInfo;
import dev.vality.alerting.mayday.ParameterValue;
import dev.vality.alerting.mayday.constant.MetricRequiredParameter;
import dev.vality.alerting.mayday.error.AlertConfigurationException;
import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;
import dev.vality.alerting.mayday.dto.CreateAlertDto;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MetricTemplateHelper {

    public static CreateAlertDto preparePrometheusRuleData(CreateAlertRequest createAlertRequest,
                                                           MetricTemplate metricTemplate,
                                                           List<MetricParam> metricParams) {

        Map<String, String> parameters = mergeParameters(createAlertRequest.getParameters(), metricParams);
        String queryExpression = prepareMetricExpression(metricTemplate, parameters);
        String alertId = generateAlertId(createAlertRequest, queryExpression);

        //TODO: validation on required params? validate types correctness?

        return CreateAlertDto.builder()
                .alertId(alertId)
                .prometheusQuery(queryExpression)
                .userId(createAlertRequest.getUserId())
                .userFriendlyAlertName(prepareUserFriendlyAlertName(metricTemplate, parameters))
                .userFriendlyAlertDescription(prepareMetricAlertMessage(metricTemplate, parameters))
                .formattedDurationMinutes(formatDuration(parameters.get(MetricRequiredParameter.ALERT_DURATION_MINUTES)))
                .build();
    }

    private static Map<String, String> mergeParameters(List<ParameterInfo> externalParamsInfo,
                                                       List<MetricParam> maydayParamsInfo) {
        return maydayParamsInfo.stream()
                .map(maydayParamInfo ->
                        {
                            var externalParamInfo = externalParamsInfo.stream()
                                    .filter(userParamInfo ->
                                            maydayParamInfo.getSubstitutionName().equals(userParamInfo.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new AlertConfigurationException("Unable to find required" +
                                            " parameter!"));
                            return new String[]{maydayParamInfo.getSubstitutionName(),
                                    extractParameterValue(externalParamInfo.getType())};
                        }
                ).collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
    }
    
    private static String extractParameterValue(ParameterValue parameterValue) {
        return switch (parameterValue.getSetField()) {
            case BL -> Boolean.toString(parameterValue.getBl());
            case INTEGER -> Long.toString(parameterValue.getInteger());
            case FL -> Double.toString(parameterValue.getFl());
            case STR -> parameterValue.getStr();
        };
    }

    private static String generateAlertId(CreateAlertRequest createAlertRequest, String preparedExpression) {
        return Integer.toString(Objects.hash(createAlertRequest, preparedExpression));
    }

    private static String prepareMetricExpression(MetricTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getQueryTemplate(), parameters);
    }

    private static String prepareUserFriendlyAlertName(MetricTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertNameTemplate(), parameters);
    }

    private static String prepareMetricAlertMessage(MetricTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertMessageTemplate(), parameters);
    }

    private static String prepareTemplate(String template, Map<String, String> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for(Map.Entry<String, String> entry : replacementsEntries) {
            preparedTemplate = preparedTemplate.replace(formatReplacementVariable(entry.getKey()), entry.getValue());
        }
        return preparedTemplate;
    }

    private static String formatReplacementVariable(String variableName) {
        return "${" + variableName + "}";
    }

    private static String formatDuration(String durationInMinutes) {
        return durationInMinutes + "m";
    }
}
