ALTER TABLE subscription_plan
    ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
