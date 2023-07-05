package dev.vality.alerting.mayday.service.helper;

import dev.vality.alerting.mayday.CreateAlertRequest;
import dev.vality.alerting.mayday.ParameterInfo;
import dev.vality.alerting.mayday.constant.AlertConfigurationRequiredParameter;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import dev.vality.alerting.mayday.error.AlertConfigurationException;
import dev.vality.alerting.mayday.model.alerttemplate.AlertTemplate;
import dev.vality.alerting.mayday.service.DictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateHelper {

    //TODO: do not pass from client empty params;
    private static final String emptyValue = "-";
    private static final String multiValueDelimiter = "|";

    private final DictionaryService dictionaryService;

    public CreateAlertDto preparePrometheusRuleData(CreateAlertRequest createAlertRequest,
                                                    AlertTemplate metricTemplate,
                                                    List<AlertTemplate.AlertConfigurationParameter>
                                                            metricParams) {

        Map<String, List<String>> parameters = mergeParameters(createAlertRequest.getParameters(), metricParams);
        String queryExpression = prepareMetricExpression(metricTemplate, parameters);
        log.debug("Prepared prometheus expression: {}", queryExpression);
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
                                .get(String.valueOf(AlertConfigurationRequiredParameter.RULE_CHECK_DURATION_MINUTES
                                        .getSubstitutionName())).get(0)))
                .build();
    }

    private Map<String, List<String>> mergeParameters(List<ParameterInfo> externalParamsInfo,
                                                      List<AlertTemplate.AlertConfigurationParameter>
                                                              maydayParamsInfo) {
        Map<String, List<String>> params = maydayParamsInfo.stream()
                .map(maydayParamInfo -> {
                            var externalParamInfos = externalParamsInfo.stream()
                                    .filter(userParamInfo ->
                                            maydayParamInfo.getId().toString().equals(userParamInfo.getId()))
                                    .toList();

                            if (externalParamInfos.size() > 1 && !maydayParamInfo.getMultipleValues()) {
                                throw new AlertConfigurationException(String.format("Parameter '%s' cannot have " +
                                        "multiple values!", maydayParamInfo.getSubstitutionName()));
                            }

                            if ((externalParamInfos.isEmpty() || externalParamInfos.size() == 1
                                    && hasNoValue(externalParamInfos.get(0))) && maydayParamInfo.getMandatory()) {
                                throw new AlertConfigurationException("Unable to find required" +
                                        " parameter: " + maydayParamInfo.getSubstitutionName());
                            }

                            List<String> values = new ArrayList<>();
                            if (!maydayParamInfo.getMandatory() && externalParamInfos.size() == 1) {
                                values.add(hasNoValue(externalParamInfos.get(0)) ? ".*" :
                                        getDictionaryValueIfRequired(maydayParamInfo, externalParamInfos.get(0)));
                            } else {
                                externalParamInfos.stream()
                                        .filter(parameterInfo -> !hasNoValue(parameterInfo))
                                        .map(parameterInfo -> getDictionaryValueIfRequired(maydayParamInfo,
                                                parameterInfo))
                                        .forEach(values::add);
                            }
                            return Map.of(maydayParamInfo.getSubstitutionName(),
                                    values);
                        }
                ).flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        //Required parameters
        Arrays.stream(AlertConfigurationRequiredParameter.values()).forEach(
                requiredParameter -> {
                    var param = getRequiredParameter(String.valueOf(requiredParameter.getId()), externalParamsInfo);
                    params.put(requiredParameter.getSubstitutionName(), List.of(param.getValue()));
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

    private String prepareMetricExpression(AlertTemplate metricTemplate, Map<String, List<String>> parameters) {
        return prepareTemplate(metricTemplate.getPrometheusQuery(), parameters);
    }

    private String prepareUserFriendlyAlertName(AlertTemplate metricTemplate, Map<String, List<String>> parameters) {
        return prepareTemplate(metricTemplate.getAlertNameTemplate(), parameters);
    }

    private String prepareMetricAlertMessage(AlertTemplate metricTemplate, Map<String, List<String>> parameters) {
        return prepareTemplate(metricTemplate.getAlertNotificationTemplate(), parameters);
    }

    private String prepareTemplate(String template, Map<String, List<String>> replacements) {
        String preparedTemplate = template;
        var replacementsEntries = replacements.entrySet();
        for (Map.Entry<String, List<String>> entry : replacementsEntries) {
            String value = entry.getValue().size() == 1
                    ? entry.getValue().get(0) : String.join(multiValueDelimiter, entry.getValue());
            preparedTemplate = preparedTemplate.replace(formatReplacementVariable(entry.getKey()), value);
        }
        return preparedTemplate;
    }

    private String getDictionaryValueIfRequired(AlertTemplate.AlertConfigurationParameter maydayParamInfo,
                                                ParameterInfo userParamInfo) {
        if (maydayParamInfo.getDictionaryName() != null) {
            return dictionaryService.getDictionary(maydayParamInfo.getDictionaryName()).get(userParamInfo.getValue());
        }
        return userParamInfo.getValue();
    }

    private String formatReplacementVariable(String variableName) {
        return "${" + variableName + "}";
    }

    private String formatDuration(String durationInMinutes) {
        return durationInMinutes + "m";
    }

    private boolean hasNoValue(ParameterInfo parameterInfo) {
        return emptyValue.equals(parameterInfo.getValue());
    }
}
