package dev.vality.alerting.mayday.service.helper;

import dev.vality.alerting.mayday.ParameterInfo;
import dev.vality.alerting.mayday.ParameterValue;
import dev.vality.alerting.mayday.constant.MetricRequiredParameter;
import dev.vality.alerting.mayday.error.AlertConfigurationException;
import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertParam;
import dev.vality.alerting.mayday.domain.tables.pojos.AlertTemplate;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TemplateHelper {

    public static CreateAlertDto preparePrometheusRuleData(CreateAlertRequest createAlertRequest,
                                                           AlertTemplate metricTemplate,
                                                           List<AlertParam> metricParams) {

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
                .formattedDurationMinutes(
                        formatDuration(parameters.get(MetricRequiredParameter.ALERT_DURATION_MINUTES)))
                .build();
    }

    private static Map<String, String> mergeParameters(List<ParameterInfo> externalParamsInfo,
                                                       List<AlertParam> maydayParamsInfo) {
        return maydayParamsInfo.stream()
                .map(maydayParamInfo -> {
                            var externalParamInfo = externalParamsInfo.stream()
                                    .filter(userParamInfo ->
                                            maydayParamInfo.getId().toString().equals(userParamInfo.getId()))
                                    .findFirst()
                                    .orElseThrow(() -> new AlertConfigurationException("Unable to find required" +
                                            " parameter: " + maydayParamInfo.getSubstitutionName()));
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
        return DigestUtils.md5DigestAsHex((createAlertRequest.getUserId() + preparedExpression)
                .getBytes(StandardCharsets.UTF_8));
    }

    private static String prepareMetricExpression(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getQueryTemplate(), parameters);
    }

    private static String prepareUserFriendlyAlertName(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertNameTemplate(), parameters);
    }

    private static String prepareMetricAlertMessage(AlertTemplate metricTemplate, Map<String, String> parameters) {
        return prepareTemplate(metricTemplate.getAlertMessageTemplate(), parameters);
    }

    private static String prepareTemplate(String template, Map<String, String> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for (Map.Entry<String, String> entry : replacementsEntries) {
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
