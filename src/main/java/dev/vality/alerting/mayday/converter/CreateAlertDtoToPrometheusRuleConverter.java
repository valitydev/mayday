package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.client.model.prometheus.PrometheusRuleSpec;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.dto.CreateAlertDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CreateAlertDtoToPrometheusRuleConverter implements Converter<CreateAlertDto, PrometheusRuleSpec.Rule> {

    @Override
    public PrometheusRuleSpec.Rule convert(CreateAlertDto source) {
        PrometheusRuleSpec.Rule rule = new PrometheusRuleSpec.Rule();
        rule.setAlert(source.getAlertId());
        rule.setExpr(source.getPrometheusQuery());
        rule.setDuration(source.getFormattedDurationMinutes());
        rule.setAnnotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, source.getUserFriendlyAlertName(),
                        PrometheusRuleAnnotation.ALERT_DESCRIPTION, source.getUserFriendlyAlertDescription()));
        return rule;
    }
}
