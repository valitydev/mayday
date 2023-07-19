package dev.vality.alerting.mayday.prometheus.converter;

import dev.vality.alerting.mayday.UserAlert;
import dev.vality.alerting.mayday.prometheus.client.k8s.model.PrometheusRuleSpec;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PrometheusRuleToUserAlertConverter implements Converter<PrometheusRuleSpec.Rule, UserAlert> {
    @Override
    public UserAlert convert(PrometheusRuleSpec.Rule source) {
        return new UserAlert()
                .setId(source.getAlert())
                .setName(source.getAnnotations().get(PrometheusRuleAnnotation.ALERT_NAME));
    }
}
