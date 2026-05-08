CREATE TABLE cars (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    engine_id INT NOT NULL UNIQUE REFERENCES engines(id)
);