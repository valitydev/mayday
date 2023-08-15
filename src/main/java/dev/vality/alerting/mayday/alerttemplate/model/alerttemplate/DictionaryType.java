package dev.vality.alerting.mayday.alerttemplate.model.alerttemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    CONDITIONAL_BOUNDARIES,
    @JsonProperty("currencies")
    CURRENCIES,
    @JsonProperty("time_interval_boundaries")
    TIME_INTERVAL_BOUNDARIES;

}
