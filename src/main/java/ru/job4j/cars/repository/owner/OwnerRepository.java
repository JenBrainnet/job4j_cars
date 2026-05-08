package ru.job4j.cars.repository.owner;

import ru.job4j.cars.model.Owner;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository {

    Owner create(Owner owner);

    void update(Owner owner);

    void delete(Integer ownerId);

    List<Owner> findAllOrderById();

    Optional<Owner> findById(Integer ownerId);

    List<Owner> findAllByUserId(Integer userId);

}