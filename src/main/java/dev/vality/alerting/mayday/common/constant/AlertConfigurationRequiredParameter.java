package dev.vality.alerting.mayday.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Обязательные параметры для создания каждого алерта
 */
@Getter
@RequiredArgsConstructor
public enum AlertConfigurationRequiredParameter {

    ALERT_REPEAT_MINUTES(Integer.MAX_VALUE - 1, "alert_repeat_minutes", "Как часто присылать повторные уведомления (в" +
            " минутах)?",
            "^\\d" +
                    "+$");

    private final int id;
    private final String substitutionName;
    private final String readableName;
    private final String regexp;
}
