package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.Alert;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class MetricTemplateToAlertConverter implements Converter<MetricTemplate, Alert> {
    @Override
    public Alert convert(MetricTemplate source) {
        return new Alert()
                .setName(source.getDisplayName());
    }
}
