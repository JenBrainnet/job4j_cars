package ru.job4j.cars.repository.owner;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Owner;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernateOwnerRepository implements OwnerRepository {

    private final CrudRepository crudRepository;

    @Override
    public Owner create(Owner owner) {
        crudRepository.run(session -> session.persist(owner));
        return owner;
    }

    @Override
    public void update(Owner owner) {
        crudRepository.run(session -> session.merge(owner));
    }

    @Override
    public void delete(Integer ownerId) {
        crudRepository.run(
                "DELETE FROM Owner WHERE id = :id",
                Map.of("id", ownerId)
        );
    }

    @Override
    public List<Owner> findAllOrderById() {
        return crudRepository.query(
                "SELECT o FROM Owner o "
                        + "JOIN FETCH o.user "
                        + "ORDER BY o.id ASC",
                Owner.class
        );
    }

    @Override
    public Optional<Owner> findById(Integer ownerId) {
        return crudRepository.optional(
                "SELECT o FROM Owner o "
                        + "JOIN FETCH o.user "
                        + "WHERE o.id = :id",
                Owner.class,
                Map.of("id", ownerId)
        );
    }

    @Override
    public List<Owner> findAllByUserId(Integer userId) {
        return crudRepository.query(
                "SELECT o FROM Owner o "
                        + "JOIN FETCH o.user "
                        + "WHERE o.user.id = :userId "
                        + "ORDER BY o.id ASC",
                Owner.class,
                Map.of("userId", userId)
        );
    }

}