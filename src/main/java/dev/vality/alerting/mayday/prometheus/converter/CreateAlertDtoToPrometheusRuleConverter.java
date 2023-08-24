package dev.vality.alerting.mayday.prometheus.converter;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import dev.vality.alerting.mayday.prometheus.config.properties.K8sPrometheusRuleProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateAlertDtoToPrometheusRuleConverter implements Converter<CreateAlertDto, PrometheusRuleSpec.Rule> {

    private final K8sPrometheusRuleProperties k8sPrometheusRuleProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public PrometheusRuleSpec.Rule convert(CreateAlertDto source) {
        PrometheusRuleSpec.Rule rule = new PrometheusRuleSpec.Rule();
        rule.setAlert(source.getAlertId());
        rule.setExpr(source.getPrometheusQuery());
        rule.setDuration(source.getFormattedDurationMinutes());
        rule.setAnnotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, source.getUserFriendlyAlertName(),
                PrometheusRuleAnnotation.ALERT_DESCRIPTION, source.getUserFriendlyAlertDescription()));
        Map<String, String> labels = new HashMap<>(k8sPrometheusRuleProperties.getAlertRule().getLabels());
        labels.put(PrometheusRuleLabel.USERNAME, source.getUserId());
        labels.put(PrometheusRuleLabel.SERVICE, applicationName);
        rule.setLabels(labels);
        return rule;
    }
}
