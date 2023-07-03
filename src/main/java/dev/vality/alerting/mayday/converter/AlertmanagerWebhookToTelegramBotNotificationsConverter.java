package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.constant.NotificationPrefix;
import dev.vality.alerting.mayday.constant.PrometheusRuleAnnotation;
import dev.vality.alerting.mayday.model.alertmanager.Webhook;
import dev.vality.alerting.tg_bot.Notification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AlertmanagerWebhookToTelegramBotNotificationsConverter
        implements Converter<Webhook, List<Notification>> {
    @Override
    public List<Notification> convert(Webhook source) {
        //TODO: throw exception if more than 1 notification?
        return source.getAlerts().stream()
                .map(alert -> convertAlertToNotification(source.getReceiver(), alert))
                .collect(Collectors.toList());
    }

    private Notification convertAlertToNotification(String receiver, Webhook.Alert alert) {
        var annotations = alert.getAnnotations();
        return new Notification()
                .setId(UUID.randomUUID().toString())
                .setReceiverId(alert.getAnnotations().get(PrometheusRuleAnnotation.USERNAME))
                .setMessage(createMessage(alert.getStatus().equals("firing"), annotations));
    }

    private String createMessage(boolean isFiring, Map<String, String> annotations) {
        String prefix = isFiring ? NotificationPrefix.ALERT_FIRING_PREFIX :
                NotificationPrefix.ALERT_RESOLVED_PREFIX;
        return prefix + annotations.get(PrometheusRuleAnnotation.ALERT_DESCRIPTION);
    }


}
