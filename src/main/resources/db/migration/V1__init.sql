CREATE SCHEMA IF NOT EXISTS md;

CREATE TABLE md.alert_template
(
    id                     uuid DEFAULT                gen_random_uuid(),
    template_name          CHARACTER VARYING           NOT NULL,
    query_template         CHARACTER VARYING           NOT NULL,
    alert_name_template    CHARACTER VARYING           NOT NULL,
    alert_message_template CHARACTER VARYING           NOT NULL,

    CONSTRAINT alert_template_pkey PRIMARY KEY (id),
    CONSTRAINT alert_template_name_unique UNIQUE (template_name),
    CONSTRAINT alert_template_query_template_unique UNIQUE (query_template)
);

CREATE TYPE md.alert_param_type AS ENUM ('bl', 'integer', 'fl', 'str');
CREATE TABLE md.alert_param
(
    id                     uuid DEFAULT                gen_random_uuid(),
    display_name           CHARACTER VARYING           NOT NULL,
    parameter_type         md.alert_param_type        NOT NULL,
    substitution_name      CHARACTER VARYING           NOT NULL,

    CONSTRAINT alert_param_pkey PRIMARY KEY (id),
    CONSTRAINT param_display_name_unique UNIQUE (display_name),
    CONSTRAINT param_substitution_name_unique UNIQUE (substitution_name)
);

CREATE TABLE md.alert_templates_to_alert_params
(
    alert_template_id     uuid                        NOT NULL,
    alert_param_id        uuid                        NOT NULL,

    CONSTRAINT alert_template_alert_param_unique UNIQUE (alert_template_id, alert_param_id),
    CONSTRAINT fk_alert_template FOREIGN KEY(alert_template_id) REFERENCES alert_template(id),
    CONSTRAINT fk_alert_param FOREIGN KEY(alert_param_id) REFERENCES alert_param(id)
);

INSERT INTO md.alert_template (template_name, query_template, alert_name_template, alert_message_template) VALUES
                                                                                                               ('Конверсия платежей по терминалу и провайдеру',
                                                                                                                'sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}", terminal_id="${terminal_id}", status="captured"}[${period_minutes}m])) / (sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}", terminal_id="${terminal_id}", status="captured"}[${period_minutes}m])) + sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}", terminal_id="${terminal_id}", status="failed"}[${period_minutes}m])))',
                                                                                                                'Конверсия платежей по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${period_minutes} минут не менее ${conversion_rate_threshold}%',
                                                                                                                'Конверсия платежей по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${period_minutes} минут меньше ${conversion_rate_threshold}%! Текущее значение: ${current_value}%');

INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Идентификатор провайдера (Пример: 123)', 'str', 'provider_id');
INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Идентификатор терминала (Пример: 123)', 'str', 'terminal_id');
INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Период в минутах, за который необходимо проверять конверсию (Пример: 60)', 'str', 'period_minutes');
INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Порог конверсии в процентах, при котором необходимо вас уведомить', 'str', 'conversion_rate_threshold');

INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия платежей по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'provider_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия платежей по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'terminal_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия платежей по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'period_minutes'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия платежей по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'conversion_rate_threshold'));