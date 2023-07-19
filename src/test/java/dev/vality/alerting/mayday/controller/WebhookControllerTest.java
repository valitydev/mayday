package dev.vality.alerting.mayday.controller;

import dev.vality.alerting.mayday.MaydayApplication;
import dev.vality.alerting.tg_bot.NotifierServiceSrv;
import org.apache.thrift.TException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {"spring.flyway.enabled=false"})
@ContextConfiguration(classes = {MaydayApplication.class})
class WebhookControllerTest {

    @LocalServerPort
    protected int localServerPort;
    @MockBean
    private NotifierServiceSrv.Iface telegramBotClient;
    private final RestTemplate restTemplateToService = new RestTemplate();

    @AfterEach
    void checkMocks() {
        verifyNoMoreInteractions(telegramBotClient);
    }


    @Test
    void alertmanagerWebhook() throws URISyntaxException, IOException, TException {
        ClassLoader classLoader = getClass().getClassLoader();
        var path = Paths.get(classLoader.getResource("webhook_example.json").toURI());
        String request = Files.readString(path, StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(request, headers);
        var result = restTemplateToService.exchange(String.format("http://localhost:%s/alertmanager/webhook",
                localServerPort), HttpMethod.POST, entity, String.class);
        assertTrue(result.getStatusCode().is2xxSuccessful());
        verify(telegramBotClient, times(1)).notify(any());
    }

}