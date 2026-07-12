-- liquibase formatted sql

-- changeset OATimofeev:001-create-recommendations-table
CREATE TABLE recommendations
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NOT NULL,
    rule_set    TEXT         NOT NULL,
    rule_code   VARCHAR(100) NOT NULL UNIQUE
);

--changeset timofeev:002-insert-invest500
INSERT INTO recommendations (id, name, description, rule_set, rule_code)
VALUES ('147f6a0f-3b91-413b-ab99-87f081d60d5a',
        'Invest 500',
        'Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка! Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель, снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!',
        'Пользователь использует как минимум один продукт с типом DEBIT.
    Пользователь не использует продукты с типом INVEST.
    Сумма пополнений продуктов с типом SAVING больше 1000 ₽.',
        'Invest500');

--changeset OATimofeev:003-insert-top-saving
INSERT INTO recommendations (id, name, description, rule_set, rule_code)
VALUES ('59efc529-2fff-41af-baff-90ccd7402925',
        'Top Saving',
        'Откройте свою собственную «Копилку» с нашим банком! «Копилка» — это уникальный банковский инструмент, который поможет вам легко и удобно накапливать деньги на важные цели. Больше никаких забытых чеков и потерянных квитанций — всё под контролем!

    Преимущества «Копилки»:
    Накопление средств на конкретные цели. Установите лимит и срок накопления, и банк будет автоматически переводить определенную сумму на ваш счет.
    Прозрачность и контроль. Отслеживайте свои доходы и расходы, контролируйте процесс накопления и корректируйте стратегию при необходимости.
    Безопасность и надежность. Ваши средства находятся под защитой банка, а доступ к ним возможен только через мобильное приложение или интернет-банкинг.

    Начните использовать «Копилку» уже сегодня и станьте ближе к своим финансовым целям!',
        'Пользователь использует как минимум один продукт с типом DEBIT.
    Сумма пополнений по всем продуктам типа DEBIT больше или равна 50 000 ₽ ИЛИ сумма пополнений по всем продуктам типа SAVING больше или равна 50 000 ₽.
    Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат по всем продуктам типа DEBIT.',
        'TopSaving');

--changeset OATimofeev:004-insert-simple-credit
INSERT INTO recommendations (id, name, description, rule_set, rule_code)
VALUES ('ab138afb-f3ba-4a93-b74f-0fcee86d447f',
        'Простой кредит',
        'Откройте мир выгодных кредитов с нами!

    Ищете способ быстро и без лишних хлопот получить нужную сумму? Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный подход к каждому клиенту.

    Почему выбирают нас:
    Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки занимает всего несколько часов.
    Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении.
    Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, лечение и многое другое.

    Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!',
        'Пользователь не использует продукты с типом CREDIT.
    Сумма пополнений по всем продуктам типа DEBIT больше, чем сумма трат по всем продуктам типа DEBIT.
    Сумма трат по всем продуктам типа DEBIT больше, чем 100 000 ₽.',
        'SimpleCredit');

-- changeset OATimofeev:005-reset-recommendations-table
ALTER TABLE recommendations
DROP
CONSTRAINT recommendations_pkey;

ALTER TABLE recommendations
    RENAME COLUMN id TO product_id;

ALTER TABLE recommendations
    ADD COLUMN id BIGINT;

CREATE SEQUENCE IF NOT EXISTS recommendations_id_seq;

ALTER TABLE recommendations
    ALTER COLUMN id SET DEFAULT nextval('recommendations_id_seq');

UPDATE recommendations
SET id = nextval('recommendations_id_seq')
WHERE id IS NULL;

ALTER TABLE recommendations
    ALTER COLUMN id SET NOT NULL;

ALTER TABLE recommendations
    ADD CONSTRAINT recommendations_pkey PRIMARY KEY (id);

ALTER TABLE recommendations
DROP
COLUMN rule_code;

ALTER TABLE recommendations
ALTER
COLUMN rule_set TYPE jsonb
    USING to_jsonb(rule_set);

ALTER SEQUENCE recommendations_id_seq
    OWNED BY recommendations.id;

-- changeset OATimofeev:006-add-rule-type-column
CREATE TYPE recommendation_rule_type AS ENUM ('STATIC', 'DYNAMIC');

ALTER TABLE recommendations
    ADD COLUMN rule_type recommendation_rule_type;

UPDATE recommendations
SET rule_type = 'STATIC'
WHERE rule_type IS NULL;

ALTER TABLE recommendations
    ALTER COLUMN rule_type SET DEFAULT 'DYNAMIC';

ALTER TABLE recommendations
    ALTER COLUMN rule_type SET NOT NULL;

-- changeset OATimofeev:007-change-rule-set-for-static-rules
UPDATE recommendations
SET rule_set = '[]'
WHERE rule_type = 'STATIC';

-- changeset OATimofeev:008-add-rule_stat-table
CREATE TABLE rule_stat
(
    id        UUID PRIMARY KEY,
    rule_id   BIGINT       NOT NULL,
    counter   BIGINT       NOT NULL DEFAULT 0,
    stat_name VARCHAR(100) NOT NULL,
    CONSTRAINT fk_rule_stat_recommendation
        FOREIGN KEY (rule_id) REFERENCES recommendations (id),
    CONSTRAINT uq_rule_stat_rule_id_stat_name
        UNIQUE (rule_id, stat_name)
);

-- changeset OATimofeev:009-init-rule-stat-for-existing-recommendations
INSERT INTO rule_stat (id, rule_id, counter, stat_name)
SELECT gen_random_uuid(), r.id, 0, 'TRIGGERED'
FROM recommendations r
WHERE NOT EXISTS (SELECT 1
                  FROM rule_stat rs
                  WHERE rs.rule_id = r.id
                    AND rs.stat_name = 'TRIGGERED');