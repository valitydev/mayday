package dev.vality.alerting.mayday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.alerting.mayday.error.AlertConfigurationException;
import dev.vality.alerting.mayday.model.alerttemplate.AlertTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class AlertConfigurationsConfig {


    @Bean
    public Map<String, AlertTemplate> alertConfigurations(ObjectMapper objectMapper, Validator validator)
            throws FileNotFoundException {
        File alertConfigsFolder = ResourceUtils.getFile(String.format("classpath:%s", "template"));
        Collection<File> files = FileUtils.listFiles(alertConfigsFolder, new String[]{"json"}, false);
        log.info("Found {} supported alert configurations", files.size());
        Map<String, AlertTemplate> templateMap = files.stream()
                .map(file -> {
                    try {
                        return objectMapper.readValue(file, AlertTemplate.class);
                    } catch (IOException e) {
                        throw new AlertConfigurationException("Unable to parse alert configuration: " +
                                file.getName(), e);
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
