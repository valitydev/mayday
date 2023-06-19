package dev.vality.alerting.mayday.controller;

import dev.vality.alerting.mayday.model.AlertmanagerWebhook;
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

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/alertmanager")
@RequiredArgsConstructor
public class AlertmanagerController {

    private final NotifierServiceSrv.Iface telegramBotClient;
    private final Converter<AlertmanagerWebhook, List<Notification>> webhookToNotificationsConverter;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity processWebhook(@RequestBody AlertmanagerWebhook webhook) {
        log.info("received smth: {}", webhook);
        var notifications = webhookToNotificationsConverter.convert(webhook);
        for (Notification notification : notifications) {
            try {
                telegramBotClient.notify(notification);
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
