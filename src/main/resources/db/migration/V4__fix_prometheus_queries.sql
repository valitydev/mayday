

UPDATE md.alert_template SET query_template = 'round(100 * sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}",status="captured", terminal_id="${terminal_id}"}[${conversion_period_minutes}m])) / sum(sum_over_time(ebm_payments_count{provider_id="${provider_id}", status=~"captured|failed",terminal_id="${terminal_id}"}[${conversion_period_minutes}m])), 1) < ${conversion_rate_threshold}'
WHERE template_name = 'Конверсия платежей по терминалу и провайдеру';
UPDATE md.alert_template SET query_template = 'round(100 * sum(sum_over_time(ebm_withdrawals_count{provider_id="${provider_id}",status="succeeded", terminal_id="${terminal_id}"}[${conversion_period_minutes}m])) / sum(sum_over_time(ebm_withdrawals_count{provider_id="${provider_id}", status=~"succeeded|failed",terminal_id="${terminal_id}"}[${conversion_period_minutes}m])), 1) < ${conversion_rate_threshold}'
WHERE template_name = 'Конверсия выплат по терминалу и провайдеру';
UPDATE md.alert_template SET query_template = 'sum(sum_over_time(ebm_payments_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m])) OR on() vector(0) < ${transaction_num_threshold}'
WHERE template_name = 'Количество платежных транзакций по терминалу и провайдеру';
UPDATE md.alert_template SET query_template = 'sum(sum_over_time(ebm_withdrawals_count{terminal_id="${terminal_id}", provider_id="${provider_id}"}[${transactions_period_minutes}m])) OR on() vector(0) < ${transaction_num_threshold}'
WHERE template_name = 'Количество выплатных транзакций по терминалу и провайдеру';