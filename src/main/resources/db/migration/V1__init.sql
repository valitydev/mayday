CREATE SCHEMA IF NOT EXISTS md;

CREATE TABLE md.metric_template
(
    id                     BIGSERIAL PRIMARY KEY,
    template_name          CHARACTER VARYING           NOT NULL,
    query_template         CHARACTER VARYING           NOT NULL,
    alert_name_template    CHARACTER VARYING           NOT NULL,
    alert_message_template CHARACTER VARYING           NOT NULL,

    CONSTRAINT metric_template_name_unique UNIQUE (template_name),
    CONSTRAINT metric_template_query_template_unique UNIQUE (query_template)
);



CREATE TYPE md.metric_param_type AS ENUM ('bl', 'integer', 'fl', 'str');
CREATE TABLE md.metric_param
(
    id                     BIGSERIAL PRIMARY KEY,
    display_name           CHARACTER VARYING           NOT NULL,
    parameter_type         md.metric_param_type        NOT NULL,
    substitution_name      CHARACTER VARYING           NOT NULL,

    CONSTRAINT param_display_name_unique UNIQUE (display_name),
    CONSTRAINT param_substitution_name_unique UNIQUE (substitution_name)
);

CREATE TABLE md.metric_templates_to_metric_params
(
    id                     BIGSERIAL PRIMARY KEY,
    metric_template_id     BIGINT                      NOT NULL,
    metric_param_id        BIGINT                      NOT NULL,

    CONSTRAINT metric_template_metric_param_unique UNIQUE (metric_template_id,metric_param_id),
    CONSTRAINT fk_metric_template FOREIGN KEY(metric_template_id) REFERENCES metric_template(id),
    CONSTRAINT fk_metric_param FOREIGN KEY(metric_param_id) REFERENCES metric_param(id)
);