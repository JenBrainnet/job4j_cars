package ru.job4j.cars.repository.post;

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
import ru.job4j.cars.model.Post;
import ru.job4j.cars.model.User;
import ru.job4j.cars.repository.car.HibernateCarRepository;
import ru.job4j.cars.repository.command.CrudRepository;
import ru.job4j.cars.repository.engine.HibernateEngineRepository;
import ru.job4j.cars.repository.user.HibernateUserRepository;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class HibernatePostRepositoryTest {

    private static StandardServiceRegistry registry;

    private static SessionFactory sf;

    private static CrudRepository crudRepository;

    private static HibernatePostRepository postRepository;

    private static HibernateUserRepository userRepository;

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
        postRepository = new HibernatePostRepository(crudRepository);
        userRepository = new HibernateUserRepository(crudRepository);
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
    public void whenCreateThenFindSamePostById() {
        var post = postRepository.create(createPost("toyota", "Toyota post"));
        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Toyota post");
        assertThat(result.get().getUser().getLogin()).isEqualTo("toyota_user");
        assertThat(result.get().getCar().getName()).isEqualTo("toyota");
        assertThat(result.get().getCar().getEngine().getName()).isEqualTo("toyota_engine");
    }

    @Test
    public void whenCreatePostWithPhotoThenFindSamePostWithPhotoById() {
        var post = postRepository.create(createPost("toyota", "Toyota post"));
        createPhoto(post, "photo.jpg", "files/photo.jpg");
        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getPhotos()).hasSize(1);
        assertThat(result.get().getPhotos().get(0).getName()).isEqualTo("photo.jpg");
        assertThat(result.get().getPhotos().get(0).getPath()).isEqualTo("files/photo.jpg");
    }

    @Test
    public void whenCreateSeveralThenFindAllOrderById() {
        postRepository.create(createPost("toyota", "Toyota post"));
        postRepository.create(createPost("bmw", "BMW post"));
        var result = postRepository.findAllOrderById();
        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly("Toyota post", "BMW post");
    }

    @Test
    public void whenUpdateThenFindUpdatedPost() {
        var post = postRepository.create(createPost("toyota", "Toyota post"));
        post.setDescription("Updated post");
        postRepository.update(post);
        var result = postRepository.findById(post.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDescription()).isEqualTo("Updated post");
    }

    @Test
    public void whenDeleteThenFindByIdReturnsEmpty() {
        var post = postRepository.create(createPost("toyota", "Toyota post"));
        postRepository.delete(post.getId());
        var result = postRepository.findById(post.getId());
        assertThat(result).isEmpty();
    }

    @Test
    public void whenFindForLastDayThenReturnOnlyNewPosts() {
        var newPost = createPost("toyota", "New post");
        newPost.setCreated(LocalDateTime.now().minusHours(1));
        var oldPost = createPost("bmw", "Old post");
        oldPost.setCreated(LocalDateTime.now().minusDays(2));
        postRepository.create(newPost);
        postRepository.create(oldPost);
        var result = postRepository.findForLastDay();
        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly("New post");
    }

    @Test
    public void whenFindWithPhotoThenReturnOnlyPostsWithPhoto() {
        var postWithPhoto = postRepository.create(createPost("toyota", "Post with photo"));
        createPhoto(postWithPhoto, "photo.jpg", "files/photo.jpg");
        postRepository.create(createPost("bmw", "Post without photo"));
        var result = postRepository.findWithPhoto();
        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly("Post with photo");
    }

    @Test
    public void whenFindByCarNameThenReturnOnlyPostsWithSameCarName() {
        postRepository.create(createPost("toyota", "Toyota post"));
        postRepository.create(createPost("bmw", "BMW post"));
        var result = postRepository.findByCarName("toyota");
        assertThat(result)
                .extracting(Post::getDescription)
                .containsExactly("Toyota post");
    }

    private Post createPost(String carName, String description) {
        var engine = new Engine();
        engine.setName(carName + "_engine");
        engineRepository.create(engine);

        var car = new Car();
        car.setName(carName);
        car.setEngine(engine);
        carRepository.create(car);

        var user = new User();
        user.setLogin(carName + "_user");
        user.setPassword("password");
        userRepository.create(user);

        var post = new Post();
        post.setDescription(description);
        post.setCreated(LocalDateTime.now());
        post.setUser(user);
        post.setCar(car);
        return post;
    }

    private void createPhoto(Post post, String name, String path) {
        crudRepository.run(session -> session.createNativeQuery(
                        "INSERT INTO photos (name, path, post_id) VALUES (:name, :path, :postId)")
                .setParameter("name", name)
                .setParameter("path", path)
                .setParameter("postId", post.getId())
                .executeUpdate());
    }

}