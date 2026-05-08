package ru.job4j.cars.repository.car;

import ru.job4j.cars.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarRepository {

    Car create(Car car);

    void update(Car car);

    void delete(Integer carId);

    List<Car> findAllOrderById();

    Optional<Car> findById(Integer carId);

    List<Car> findByEngineId(Integer engineId);

}