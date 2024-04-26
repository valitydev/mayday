package dev.vality.alerting.mayday.common.dto;

import dev.vality.alerting.mayday.alerttemplate.model.dictionary.DictionaryData;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class CreateAlertDto {
    private String alertId;
    private String userId;
    private String prometheusQuery;
    private String userFriendlyAlertName;
    private String userFriendlyAlertDescription;
    private Map<String, List<DictionaryData>> parameters;
}
