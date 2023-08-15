package dev.vality.alerting.mayday.alerttemplate.model.daway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Currency {
    private String symbolicCode;
    private String name;
}
