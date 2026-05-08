CREATE TABLE history_owners (
    id SERIAL PRIMARY KEY,
    owner_id INT NOT NULL REFERENCES owners(id),
    car_id INT NOT NULL REFERENCES cars(id),
    UNIQUE (owner_id, car_id)
);