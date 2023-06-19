package dev.vality.alerting.mayday.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MetricRequiredParameter {

    RULE_CHECK_DURATION_MINUTES("rule_check_duration_minutes", "Как долго условие должно выполняться (в минутах)," +
            "прежде чем " +
            "отправить уведомление?"),
    ALERT_REPEAT_MINUTES("alert_repeat_minutes", "Как часто присылать повторные уведомления (в минутах)?");

    private final String parameterTemplate;
    private final String parameterName;
}
