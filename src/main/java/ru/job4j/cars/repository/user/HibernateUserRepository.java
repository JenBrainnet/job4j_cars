package ru.job4j.cars.repository.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernateUserRepository implements UserRepository {

    private final CrudRepository crudRepository;

    /**
     * Save a user to the database.
     * @param user user.
     * @return user with id.
     */
    @Override
    public User create(User user) {
        crudRepository.run(session -> session.persist(user));
        return user;
    }

    /**
     * Update a user in the database.
     * @param user user.
     */
    @Override
    public void update(User user) {
        crudRepository.run(session -> session.merge(user));
    }

    /**
     * Delete a user by id.
     * @param userId ID
     */
    @Override
    public void delete(Integer userId) {
        crudRepository.run(
                "DELETE FROM User WHERE id = :id",
                Map.of("id", userId)
        );
    }

    /**
     * Get the list of users sorted by id.
     * @return list of users.
     */
    @Override
    public List<User> findAllOrderById() {
        return crudRepository.query("FROM User ORDER BY id ASC", User.class);
    }

    /**
     * Find a user by ID
     * @param userId user ID
     * @return user.
     */
    @Override
    public Optional<User> findById(Integer userId) {
        return crudRepository.optional(
                "FROM User WHERE id = :id", User.class,
                Map.of("id", userId)
        );
    }

    /**
     * Get the list of users by login LIKE %key%
     * @param key key
     * @return list of users.
     */
    @Override
    public List<User> findByLikeLogin(String key) {
        return crudRepository.query(
                "FROM User WHERE login LIKE :key ORDER BY id ASC", User.class,
                Map.of("key", "%" + key + "%")
        );
    }

    /**
     * Find a user by login.
     * @param login login.
     * @return Optional or user.
     */
    @Override
    public Optional<User> findByLogin(String login) {
        return crudRepository.optional(
                "FROM User WHERE login = :login", User.class,
                Map.of("login", login)
        );
    }

}
