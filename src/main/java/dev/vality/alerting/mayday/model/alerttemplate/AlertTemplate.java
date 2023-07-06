package dev.vality.alerting.mayday.model.alerttemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.vality.alerting.mayday.constant.DictionaryType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class AlertTemplate {

    @NotNull
    private final String id;
    @NotNull
    @JsonProperty("readable_name")
    private final String readableName;
    @NotNull
    @JsonProperty("prometheus_query")
    private final String prometheusQuery;
    @NotNull
    @JsonProperty("alert_name_template")
    private final String alertNameTemplate;
    @NotNull
    @JsonProperty("alert_notification_template")
    private final String alertNotificationTemplate;
    private List<AlertConfigurationParameter> parameters;

    @Data
    public static class AlertConfigurationParameter {
        @NotNull
        private final Integer id;
        @NotNull
        @JsonProperty("substitution_name")
        private final String substitutionName;
        @NotNull
        @JsonProperty("readable_name")
        private final String readableName;
        @NotNull
        private final Boolean mandatory;
        @JsonProperty(value = "multiple_values")
        private final Boolean multipleValues = false;
        @JsonProperty("dictionary_name")
        private final DictionaryType dictionaryName;
        private final String regexp;
    }

}
