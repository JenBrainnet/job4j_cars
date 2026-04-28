package ru.job4j.cars.repository;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import ru.job4j.cars.model.User;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class UserRepository {

    private final SessionFactory sf;

    /**
     * Save a user to the database.
     * @param user user.
     * @return user with id.
     */
    public User create(User user) {
        try (Session session = sf.openSession()) {
            var tx = session.beginTransaction();
            try {
                session.save(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            }
        }
        return user;
    }

    /**
     * Update a user in the database.
     * @param user user.
     */
    public void update(User user) {
        try (Session session = sf.openSession()) {
            var tx = session.beginTransaction();
            try {
                session.update(user);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            }
        }
    }

    /**
     * Delete a user by id.
     * @param userId ID
     */
    public void delete(Integer userId) {
        try (Session session = sf.openSession()) {
            var tx = session.beginTransaction();
            try {
                session.createQuery("DELETE User WHERE id = :id")
                        .setParameter("id", userId)
                        .executeUpdate();
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
            }
        }
    }

    /**
     * Get the list of users sorted by id.
     * @return list of users.
     */
    public List<User> findAllOrderById() {
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM User ORDER BY id ASC", User.class)
                    .list();
        }
    }

    /**
     * Find a user by ID
     * @param userId user ID
     * @return user.
     */
    public Optional<User> findById(Integer userId) {
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM User WHERE id = :id", User.class)
                    .setParameter("id", userId)
                    .uniqueResultOptional();
        }
    }

    /**
     * Get the list of users by login LIKE %key%
     * @param key key
     * @return list of users.
     */
    public List<User> findByLikeLogin(String key) {
        try (Session session = sf.openSession()) {
            return session.createQuery(
                    "FROM User WHERE login LIKE :key ORDER BY id ASC", User.class)
                    .setParameter("key", "%" + key + "%")
                    .list();
        }
    }

    /**
     * Find a user by login.
     * @param login login.
     * @return Optional or user.
     */
    public Optional<User> findByLogin(String login) {
        try (Session session = sf.openSession()) {
            return session.createQuery("FROM User WHERE login = :login", User.class)
                    .setParameter("login", login)
                    .uniqueResultOptional();
        }
    }

}
