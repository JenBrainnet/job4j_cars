package ru.job4j.cars.repository.car;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.Car;
import ru.job4j.cars.model.Engine;
import ru.job4j.cars.repository.command.CrudRepository;
import ru.job4j.cars.repository.engine.HibernateEngineRepository;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateCarRepositoryTest {

    private static StandardServiceRegistry registry;

    private static SessionFactory sf;

    private static CrudRepository crudRepository;

    private static HibernateCarRepository carRepository;

    private static HibernateEngineRepository engineRepository;

    @BeforeAll
    public static void initRepositories() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sf);
        carRepository = new HibernateCarRepository(crudRepository);
        engineRepository = new HibernateEngineRepository(crudRepository);
    }

    @BeforeEach
    public void clearTables() {
        crudRepository.run(session -> {
            session.createNativeQuery("DELETE FROM photos").executeUpdate();
            session.createNativeQuery("DELETE FROM price_history").executeUpdate();
            session.createNativeQuery("DELETE FROM post_subscribers").executeUpdate();
            session.createNativeQuery("DELETE FROM posts").executeUpdate();
            session.createNativeQuery("DELETE FROM history_owners").executeUpdate();
            session.createNativeQuery("DELETE FROM owners").executeUpdate();
            session.createNativeQuery("DELETE FROM cars").executeUpdate();
            session.createNativeQuery("DELETE FROM engines").executeUpdate();
            session.createNativeQuery("DELETE FROM users").executeUpdate();
        });
    }

    @AfterAll
    public static void closeSessionFactory() {
        if (sf != null) {
            sf.close();
        }
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

    @Test
    public void whenCreateThenFindSameCarById() {
        var car = carRepository.create(createCar("toyota", "toyota_engine"));
        var result = carRepository.findById(car.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("toyota");
        assertThat(result.get().getEngine().getName()).isEqualTo("toyota_engine");
    }

    @Test
    public void whenCreateSeveralThenFindAllOrderById() {
        carRepository.create(createCar("toyota", "toyota_engine"));
        carRepository.create(createCar("bmw", "bmw_engine"));
        var result = carRepository.findAllOrderById();
        assertThat(result)
                .extracting(Car::getName)
                .containsExactly("toyota", "bmw");
    }

    @Test
    public void whenUpdateThenFindUpdatedCar() {
        var car = carRepository.create(createCar("toyota", "toyota_engine"));
        car.setName("updated_toyota");
        carRepository.update(car);
        var result = carRepository.findById(car.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("updated_toyota");
    }

    @Test
    public void whenDeleteThenFindByIdReturnsEmpty() {
        var car = carRepository.create(createCar("toyota", "toyota_engine"));
        carRepository.delete(car.getId());
        var result = carRepository.findById(car.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void whenFindByEngineIdThenReturnOnlyCarsWithSameEngine() {
        var first = carRepository.create(createCar("toyota", "toyota_engine"));
        carRepository.create(createCar("bmw", "bmw_engine"));
        var result = carRepository.findByEngineId(first.getEngine().getId());
        assertThat(result)
                .extracting(Car::getName)
                .containsExactly("toyota");
    }

    private Car createCar(String carName, String engineName) {
        var engine = new Engine();
        engine.setName(engineName);
        engineRepository.create(engine);

        var car = new Car();
        car.setName(carName);
        car.setEngine(engine);
        return car;
    }

}