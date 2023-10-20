package dev.vality.alerting.mayday.prometheus.client.k8s.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Kind;
import io.fabric8.kubernetes.model.annotation.Version;


@Version("v1")
@Group("monitoring.coreos.com")
@Kind("PrometheusRule")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrometheusRule extends CustomResource<PrometheusRuleSpec, PrometheusRuleStatus> implements Namespaced {
}
