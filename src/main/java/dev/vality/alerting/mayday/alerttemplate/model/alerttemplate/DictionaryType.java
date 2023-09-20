package dev.vality.alerting.mayday.alerttemplate.model.alerttemplate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DictionaryType {

    @JsonProperty("terminals")
    TERMINALS,
    @JsonProperty("payment_terminals")
    PAYMENT_TERMINALS,
    @JsonProperty("payout_terminals")
    PAYOUT_TERMINALS,
    @JsonProperty("providers")
    PROVIDERS,
    @JsonProperty("payment_providers")
    PAYMENT_PROVIDERS,
    @JsonProperty("payout_providers")
    PAYOUT_PROVIDERS,
    @JsonProperty("wallets")
    WALLETS,
    @JsonProperty("shops")
    SHOPS,
    @JsonProperty("payment_limit_scopes")
    PAYMENT_LIMIT_SCOPES,
    @JsonProperty("payout_limit_scopes")
    PAYOUT_LIMIT_SCOPES,
    @JsonProperty("boundaries")
    CONDITIONAL_BOUNDARIES,
    @JsonProperty("currencies")
    CURRENCIES,
    @JsonProperty("time_interval_boundaries")
    TIME_INTERVAL_BOUNDARIES;

}
