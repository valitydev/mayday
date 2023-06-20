
UPDATE md.alert_param SET substitution_name = 'conversion_period_minutes' WHERE substitution_name = 'period_minutes';
UPDATE md.alert_template SET query_template = 'round(100 * sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}",status="captured", terminal_id="${terminal_id}"}[${conversion_period_minutes}m])) / sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}", status=~"captured|failed",terminal_id="${terminal_id}"}[${conversion_period_minutes}m])), 1)'
                         WHERE template_name = 'Конверсия платежей по терминалу и провайдеру';



-- Метрика с количеством платежных транзакций
INSERT INTO md.alert_template (template_name, query_template, alert_name_template, alert_message_template) VALUES
    ('Количество платежных транзакций по терминалу и провайдеру',
     'sum(sum_over_time(ebm_payments_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m]))',
     'Количество платежных транзакций у провайдера ${provider_id} и терминала ${terminal_id} за последние ${transactions_period_minutes} минут не менее ${transaction_num_threshold}',
     'Количество платежных транзакций у провайдера ${provider_id} и терминала ${terminal_id} за последние ${transactions_period_minutes} минут меньше ${transaction_num_threshold}! Текущее значение: {{ $value }}');

INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Период в минутах, за который необходимо считать количество транзакций (Пример: 60)', 'str', 'transactions_period_minutes');
INSERT INTO md.alert_param (display_name, parameter_type, substitution_name) VALUES ('Порог количества транзакций, при котором необходимо вас уведомить', 'str', 'transaction_num_threshold');

INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество платежных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'provider_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество платежных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'terminal_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество платежных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'transactions_period_minutes'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество платежных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'transaction_num_threshold'));

-- Метрика с количеством выплатных транзакций
INSERT INTO md.alert_template (template_name, query_template, alert_name_template, alert_message_template) VALUES
    ('Количество выплатных транзакций по терминалу и провайдеру',
     'sum(sum_over_time(ebm_withdrawals_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m]))',
     'Количество выплатных транзакций у провайдера ${provider_id} и терминала ${terminal_id} за последние ${transactions_period_minutes} минут не менее ${transaction_num_threshold}',
     'Количество выплатных транзакций у провайдера ${provider_id} и терминала ${terminal_id} за последние ${transactions_period_minutes} минут меньше ${transaction_num_threshold}! Текущее значение: {{ $value }}');

INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество выплатных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'provider_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество выплатных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'terminal_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество выплатных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'transactions_period_minutes'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Количество выплатных транзакций по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'transaction_num_threshold'));

-- Метрика с конверсией по выплатам
INSERT INTO md.alert_template (template_name, query_template, alert_name_template, alert_message_template) VALUES
    ('Конверсия выплат по терминалу и провайдеру',
     'round(100 * sum(sum_over_time(ebm_withdrawals_count{provider_id="${provider_id}",status="succeeded", terminal_id="${terminal_id}"}[${conversion_period_minutes}m])) / sum(sum_over_time(ebm_withdrawals_count{provider_id="${provider_id}", status=~"succeeded|failed",terminal_id="${terminal_id}"}[${conversion_period_minutes}m])), 1)',
     'Конверсия выплат по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${conversion_period_minutes} минут не менее ${conversion_rate_threshold}%',
     'Конверсия выплат по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${conversion_period_minutes} минут меньше ${conversion_rate_threshold}%! Текущее значение: {{ $value }}%');

INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия выплат по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'provider_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия выплат по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'terminal_id'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия выплат по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'conversion_period_minutes'));
INSERT INTO md.alert_templates_to_alert_params (alert_template_id, alert_param_id) VALUES ((SELECT id FROM md.alert_template WHERE template_name = 'Конверсия выплат по терминалу и провайдеру'),
                                                                                           (SELECT id FROM md.alert_param WHERE substitution_name = 'conversion_rate_threshold'));