package dev.vality.alerting.mayday.thrift.converter;

import dev.vality.alerting.mayday.Alert;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.AlertTemplate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AlertTemplateToAlertConverter implements Converter<AlertTemplate, Alert> {
    @Override
    public Alert convert(AlertTemplate source) {
        return new Alert()
                .setId(source.getId())
                .setName(source.getReadableName());
    }
}
