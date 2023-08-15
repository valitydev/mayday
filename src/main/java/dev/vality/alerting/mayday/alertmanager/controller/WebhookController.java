package dev.vality.alerting.mayday.alertmanager.controller;

import dev.vality.alerting.mayday.alertmanager.model.Webhook;
import dev.vality.alerting.mayday.alertmanager.service.AlertmanagerService;
import dev.vality.alerting.mayday.common.constant.PrometheusRuleLabel;
import dev.vality.alerting.tg_bot.Notification;
import dev.vality.alerting.tg_bot.NotifierServiceSrv;
import dev.vality.alerting.tg_bot.ReceiverNotFound;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.thrift.TException;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/alertmanager")
@RequiredArgsConstructor
public class WebhookController {

    private final NotifierServiceSrv.Iface telegramBotClient;
    private final Converter<Webhook.Alert, Notification> webhookAlertToNotificationConverter;
    private final AlertmanagerService alertmanagerService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> processWebhook(@RequestBody Webhook webhook) {
        log.info("Received webhook from alertmanager: {}", webhook);
        for (Webhook.Alert alert : webhook.getAlerts()) {
            try {
                String userId = alert.getLabels().get(PrometheusRuleLabel.USERNAME);
                String alertName = alert.getLabels().get(PrometheusRuleLabel.ALERT_NAME);
                // Алертменеджер может прислать нотификацию уже после того, как пользователь удалил алерт, т.к
                // обновления в конфигурации применяются не моментально. Поэтому нужна доп.фильтрация здесь.
                if (alertmanagerService.containsUserRoute(userId, alertName)) {
                    var notification = webhookAlertToNotificationConverter.convert(alert);
                    telegramBotClient.notify(notification);
                    log.info("Alertmanager webhook processed successfully: {}", webhook);
                }
            } catch (ReceiverNotFound receiverNotFound) {
                log.error("Unable to find notification receiver '{}':", webhook.getReceiver(), receiverNotFound);
            } catch (TException e) {
                log.error("Unexpected error during notification delivery:", e);
                return ResponseEntity.internalServerError().build();
            }
        }
        return ResponseEntity.ok().build();
    }
}
