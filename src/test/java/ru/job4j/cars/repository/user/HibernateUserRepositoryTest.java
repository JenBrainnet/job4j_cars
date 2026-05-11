package ru.job4j.cars.repository.user;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.command.CrudRepository;

import static org.assertj.core.api.Assertions.assertThat;

class HibernateUserRepositoryTest {

    private static StandardServiceRegistry registry;

    private static SessionFactory sf;

    private static CrudRepository crudRepository;

    private static HibernateUserRepository userRepository;

    @BeforeAll
    public static void initRepositories() {
        registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        sf = new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
        crudRepository = new CrudRepository(sf);
        userRepository = new HibernateUserRepository(crudRepository);
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
    public void whenCreateThenFindSameUserById() {
        var user = userRepository.create(createUser("login"));
        var result = userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("login");
        assertThat(result.get().getPassword()).isEqualTo("password");
    }

    @Test
    public void whenCreateSeveralThenFindAllOrderById() {
        userRepository.create(createUser("first"));
        userRepository.create(createUser("second"));
        var result = userRepository.findAllOrderById();
        assertThat(result)
                .extracting(User::getLogin)
                .containsExactly("first", "second");
    }

    @Test
    public void whenUpdateThenFindUpdatedUser() {
        var user = userRepository.create(createUser("login"));
        user.setLogin("updated_login");
        user.setPassword("updated_password");
        userRepository.update(user);
        var result = userRepository.findById(user.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("updated_login");
        assertThat(result.get().getPassword()).isEqualTo("updated_password");
    }

    @Test
    public void whenDeleteThenFindByIdReturnsEmpty() {
        var user = userRepository.create(createUser("login"));
        userRepository.delete(user.getId());
        var result = userRepository.findById(user.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void whenFindByLikeLoginThenReturnMatchedUsers() {
        userRepository.create(createUser("user1"));
        userRepository.create(createUser("user2"));
        userRepository.create(createUser("another"));
        var result = userRepository.findByLikeLogin("user");
        assertThat(result)
                .extracting(User::getLogin)
                .containsExactly("user1", "user2");
    }

    @Test
    public void whenFindByLoginThenReturnSameUser() {
        userRepository.create(createUser("login"));
        var result = userRepository.findByLogin("login");
        assertThat(result).isPresent();
        assertThat(result.get().getLogin()).isEqualTo("login");
    }

    private User createUser(String login) {
        var user = new User();
        user.setLogin(login);
        user.setPassword("password");
        return user;
    }
}
