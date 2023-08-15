package dev.vality.alerting.mayday.alerttemplate.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.alerting.mayday.alerttemplate.error.AlertConfigurationException;
import dev.vality.alerting.mayday.alerttemplate.model.alerttemplate.AlertTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AlertConfigurationsConfig {


    @Bean
    public Map<String, AlertTemplate> alertConfigurations(ResourcePatternResolver resourcePatternResolver,
                                                          ObjectMapper objectMapper,
                                                          Validator validator)
            throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath:template/*.json");
        log.info("Found {} supported alert configurations", resources.length);
        Map<String, AlertTemplate> templateMap = Arrays.stream(resources)
                .map(resource -> {
                    try {
                        return objectMapper.readValue(resource.getURL(), AlertTemplate.class);
                    } catch (IOException e) {
                        throw new AlertConfigurationException("Unable to parse alert configuration: " +
                                resource.getFilename(), e);
                    }
                }).collect(Collectors.toMap(AlertTemplate::getId, alertTemplate -> alertTemplate));

        Set<ConstraintViolation<AlertTemplate>> violations =
                templateMap.entrySet().stream().flatMap(entry -> validator.validate(entry.getValue()).stream())
                        .collect(Collectors.toSet());
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return templateMap;
    }
}
