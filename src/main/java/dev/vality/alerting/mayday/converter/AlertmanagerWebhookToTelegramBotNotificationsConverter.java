package dev.vality.alerting.mayday.converter;

import dev.vality.alerting.mayday.model.AlertmanagerWebhook;
import dev.vality.alerting.tg_bot.Notification;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class AlertmanagerWebhookToTelegramBotNotificationsConverter
        implements Converter<AlertmanagerWebhook, List<Notification>> {
    @Override
    public List<Notification> convert(AlertmanagerWebhook source) {
        //TODO: throw exception if more than 1 notification?
        return source.getAlerts().stream()
                .map(alert -> convertAlertToNotification(source.getReceiver(), alert))
                .collect(Collectors.toList());
    }

    private Notification convertAlertToNotification(String receiver, AlertmanagerWebhook.Alert alert) {
        return new Notification()
                .setId(UUID.randomUUID().toString())
                .setReceiverId(receiver)
                //TODO: convert to smth readable
                .setMessage(alert.getAnnotations());
    }


}
