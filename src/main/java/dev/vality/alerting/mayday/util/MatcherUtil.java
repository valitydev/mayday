package dev.vality.alerting.mayday.util;

import dev.vality.alerting.mayday.client.model.alertmanager.AlertmanagerConfigSpec;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatcherUtil {

    public static AlertmanagerConfigSpec.Matcher createUserMatcher(String labelName, String labelValue) {
        AlertmanagerConfigSpec.Matcher matcher = new AlertmanagerConfigSpec.Matcher();
        matcher.setName(labelName);
        matcher.setValue(labelValue);
        matcher.setMatchType("=");
        return matcher;
    }
}
