package dev.vality.alerting.mayday.prometheus.converter;

import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.mayday.common.dto.CreateAlertDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class CreateAlertDtoToPrometheusRuleConverter implements Converter<CreateAlertDto, PrometheusRuleSpec.Rule> {

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
        // Лейбл с неймспейсом необходим, поскольку алертменеджер по умолчанию начинает фильтровать по нему.
        // Тут описано более подробно: https://github.com/prometheus-operator/prometheus-operator/discussions/3733
        rule.setLabels(Map.of(PrometheusRuleLabel.NAMESPACE, "default",
                PrometheusRuleLabel.USERNAME, source.getUserId(),
                PrometheusRuleLabel.SERVICE, applicationName));
        return rule;
    }
}
