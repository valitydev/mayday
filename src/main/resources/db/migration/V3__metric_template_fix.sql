UPDATE md.alert_template SET alert_name_template = 'Конверсия платежей по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${conversion_period_minutes} минут не менее ${conversion_rate_threshold}%'
    WHERE template_name = 'Конверсия платежей по терминалу и провайдеру';
UPDATE md.alert_template SET alert_message_template = 'Конверсия платежей по провайдеру ${provider_id} и терминалу ${terminal_id} за последние ${conversion_period_minutes} минут меньше ${conversion_rate_threshold}%! Текущее значение: {{ $value }}%'
    WHERE template_name = 'Конверсия платежей по терминалу и провайдеру';

UPDATE md.alert_template SET query_template = 'sum(sum_over_time(ebm_payments_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m])) OR on() vector(0)'
    WHERE  template_name = 'Количество платежных транзакций по терминалу и провайдеру';
UPDATE md.alert_template SET query_template = 'sum(sum_over_time(ebm_withdrawals_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m])) OR on() vector(0)'
    WHERE  template_name = 'Количество выплатных транзакций по терминалу и провайдеру';