package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.config.properties.KubernetesProperties;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateAlertDtoToPrometheusRuleConverter implements Converter<CreateAlertDto, PrometheusRuleSpec.Rule> {

    private final KubernetesProperties kubernetesProperties;

    @Override
    public PrometheusRuleSpec.Rule convert(CreateAlertDto source) {
        PrometheusRuleSpec.Rule rule = new PrometheusRuleSpec.Rule();
        rule.setAlert(source.getAlertId());
        rule.setExpr(source.getPrometheusQuery());
        rule.setDuration(source.getFormattedDurationMinutes());
        rule.setAnnotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, source.getUserFriendlyAlertName(),
                PrometheusRuleAnnotation.USERNAME, source.getUserId(),
                PrometheusRuleAnnotation.ALERT_DESCRIPTION, source.getUserFriendlyAlertDescription()));
        // Лейбл с неймспейсом необходим, поскольку алертменеджер по умолчанию начинает фильтровать по нему.
        // Тут описано более подробно: https://github.com/prometheus-operator/prometheus-operator/discussions/3733
        rule.setLabels(Map.of(PrometheusRuleLabel.NAMESPACE_LABEL_NAME, kubernetesProperties.getNamespace(),
                PrometheusRuleLabel.USERNAME_LABEL_NAME, source.getUserId()));
        return rule;
    }
}
