package dev.vality.alerting.mayday.client.model.alertmanager;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("monitoring.coreos.com")
@Kind("AlertmanagerConfig")
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertmanagerConfig extends CustomResource<AlertmanagerConfigSpec, AlertmanagerConfigStatus>
        implements Namespaced {
}
