package dev.vality.alerting.mayday.client.model.prometheus;

import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;


@Version("v1")
@Group("monitoring.coreos.com")
public class PrometheusRule extends CustomResource<PrometheusRuleSpec, PrometheusRuleStatus> implements Namespaced {
}
