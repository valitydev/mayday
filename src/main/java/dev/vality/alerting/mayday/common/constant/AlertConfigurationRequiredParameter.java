package dev.vality.alerting.mayday.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Обязательные параметры для создания каждого алерта
 */
@Getter
@RequiredArgsConstructor
public enum AlertConfigurationRequiredParameter {

    RULE_CHECK_DURATION_MINUTES(Integer.MAX_VALUE, "rule_check_duration_minutes", "Как долго условие должно " +
            "выполняться (в " +
            "минутах)," +
            "прежде чем " +
            "отправить уведомление?",
            "^\\d+$"),
    ALERT_REPEAT_MINUTES(Integer.MAX_VALUE - 1, "alert_repeat_minutes", "Как часто присылать повторные уведомления (в" +
            " минутах)?",
            "^\\d" +
                    "+$");

    private final int id;
    private final String substitutionName;
    private final String readableName;
    private final String regexp;
}
