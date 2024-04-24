package dev.vality.alerting.mayday.alerttemplate.model.dictionary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DictionaryData {

    private String value;
    private String userFriendlyValue;

    public DictionaryData(String value) {
        this.value = value;
    }

    public String getUserFriendlyValue() {
        return userFriendlyValue != null ? userFriendlyValue : value;
    }
}
