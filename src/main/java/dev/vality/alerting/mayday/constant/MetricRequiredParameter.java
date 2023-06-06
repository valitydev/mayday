package dev.vality.alerting.mayday.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricRequiredParameter {

    ALERT_DURATION_MINUTES("alert_duration_minutes", "Как часто присылать уведомления (в минутах)");

    private final String parameterTemplate;
    private final String parameterName;
}
