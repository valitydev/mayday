package dev.vality.alerting.mayday.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CreateAlertDto {

    private String alertId;
    private String userId;
    private String prometheusQuery;
    private String userFriendlyAlertName;
    private String userFriendlyAlertDescription;
    private Map<String, String> parameters;
    private String formattedDurationMinutes;
}
