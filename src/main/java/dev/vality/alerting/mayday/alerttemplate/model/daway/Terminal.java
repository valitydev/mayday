package dev.vality.alerting.mayday.alerttemplate.model.daway;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Terminal {
    private Integer id;
    private String name;
}
