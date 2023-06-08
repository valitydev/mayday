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
        return PrometheusRuleSpec.Rule.builder()
                .alert(source.getAlertId())
                .expr(source.getPrometheusQuery())
                .duration(source.getFormattedDurationMinutes())
                .annotations(Map.of(PrometheusRuleAnnotation.ALERT_NAME, source.getUserFriendlyAlertName()))
                .build();
    }
}
