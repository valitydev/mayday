package dev.vality.alerting.mayday.service;

import dev.vality.alerting.mayday.*;
import dev.vality.alerting.mayday.dao.MetricParamDao;
import dev.vality.alerting.mayday.dao.MetricTemplateDao;
import dev.vality.alerting.mayday.domain.enums.MetricParamType;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricParam;
import dev.vality.alerting.mayday.domain.tables.pojos.MetricTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricTemplateService {

    private final MetricTemplateDao metricTemplateDao;
    private final MetricParamDao metricParamDao;

    public MetricTemplate getMetricTemplateById(Long metricTemplateId) {
        return metricTemplateDao.findById(metricTemplateId);
    }

    public List<MetricTemplate> getAllMetricTemplates() {
        return metricTemplateDao.findAll();
    }

    public List<MetricParam> getMetricParams(String templateId) {
        return metricParamDao.findAllByMetricTemplateId(Long.valueOf(templateId));
    }

    public void getPreparedMetric(CreateAlertRequest createAlertRequest) {
        MetricTemplate template = metricTemplateDao.findById(Long.valueOf(createAlertRequest.getAlertId()));
        String queryTemplate = template.getQueryTemplate();
        List<MetricParam> metricParams = metricParamDao.findAllByMetricTemplateId(template.getId());
        createAlertRequest.getParameters().stream().forEach(parameterInfo -> {
            Optional<MetricParam> metricParam =
                    metricParams.stream().filter(metricParam1 -> metricParam1.getId().equals(Long.valueOf(parameterInfo.getId()))).findFirst();
        });
    }


}
