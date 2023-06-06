package dev.vality.alerting.mayday.client.model.alertmanager;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1alpha1")
@Group("monitoring.coreos.com")
public class AlertmanagerConfig extends CustomResource<AlertmanagerConfigSpec, AlertmanagerConfigStatus>
        implements Namespaced {
}
