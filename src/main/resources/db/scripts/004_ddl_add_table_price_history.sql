CREATE TABLE price_history (
    id SERIAL PRIMARY KEY,
    before BIGINT NOT NULL,
    after BIGINT NULL,
    created TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
    auto_post_id INT REFERENCES auto_post(id)
);