package dev.vality.alerting.mayday.constant;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DictionaryType {

    @JsonProperty("terminals")
    TERMINALS,
    @JsonProperty("providers")
    PROVIDERS,
    @JsonProperty("wallets")
    WALLETS,
    @JsonProperty("shops")
    SHOPS,
    @JsonProperty("boundaries")
    BOUNDARIES;

}
