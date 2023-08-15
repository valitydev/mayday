package dev.vality.alerting.mayday.alertmanager.converter;

import dev.vality.alerting.mayday.alertmanager.constant.NotificationPrefix;
import dev.vality.alerting.mayday.alertmanager.constant.WebhookStatus;
import dev.vality.alerting.mayday.alertmanager.model.Webhook;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.tg_bot.Notification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class AlertmanagerWebhookAlertToTelegramBotNotificationsConverter
        implements Converter<Webhook.Alert, Notification> {
    @Override
    public Notification convert(Webhook.Alert alert) {
        var annotations = alert.getAnnotations();
        return new Notification()
                .setId(UUID.randomUUID().toString())
                .setReceiverId(alert.getLabels().get(PrometheusRuleLabel.USERNAME))
                .setMessage(createMessage(alert.getStatus().equals(WebhookStatus.FIRING), annotations));
    }

    private String createMessage(boolean isFiring, Map<String, String> annotations) {
        String prefix = isFiring ? NotificationPrefix.ALERT_FIRING_PREFIX :
                NotificationPrefix.ALERT_RESOLVED_PREFIX;
        return prefix + annotations.get(PrometheusRuleAnnotation.ALERT_DESCRIPTION);
    }


}
