package ru.job4j.cars.repository.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.command.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernatePostRepository implements PostRepository {

    private final CrudRepository crudRepository;

    @Override
    public Post create(Post post) {
        crudRepository.run(session -> session.persist(post));
        return post;
    }

    @Override
    public void update(Post post) {
        crudRepository.run(session -> session.merge(post));
    }

    @Override
    public void delete(Integer postId) {
        crudRepository.run(session -> {
            var post = session.get(Post.class, postId);
            if (post != null) {
                session.delete(post);
            }
        });
    }

    @Override
    public List<Post> findAllOrderById() {
        return crudRepository.query(
                "SELECT DISTINCT p FROM Post p "
                        + "JOIN FETCH p.user "
                        + "JOIN FETCH p.car c "
                        + "JOIN FETCH c.engine "
                        + "LEFT JOIN FETCH p.photos "
                        + "ORDER BY p.id ASC",
                Post.class
        );
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return crudRepository.optional(
                "SELECT DISTINCT p FROM Post p "
                        + "JOIN FETCH p.user "
                        + "JOIN FETCH p.car c "
                        + "JOIN FETCH c.engine "
                        + "LEFT JOIN FETCH p.photos "
                        + "WHERE p.id = :id",
                Post.class,
                Map.of("id", postId)
        );
    }

    @Override
    public List<Post> findForLastDay() {
        return crudRepository.query(
                "SELECT DISTINCT p FROM Post p "
                        + "JOIN FETCH p.user "
                        + "JOIN FETCH p.car c "
                        + "JOIN FETCH c.engine "
                        + "LEFT JOIN FETCH p.photos "
                        + "WHERE p.created >= :dateFrom "
                        + "ORDER BY p.created DESC",
                Post.class,
                Map.of("dateFrom", LocalDateTime.now().minusDays(1))
        );
    }

    @Override
    public List<Post> findWithPhoto() {
        return crudRepository.query(
                "SELECT DISTINCT p FROM Post p "
                        + "JOIN FETCH p.user "
                        + "JOIN FETCH p.car c "
                        + "JOIN FETCH c.engine "
                        + "JOIN FETCH p.photos "
                        + "ORDER BY p.created DESC",
                Post.class
        );
    }

    @Override
    public List<Post> findByCarName(String carName) {
        return crudRepository.query(
                "SELECT DISTINCT p FROM Post p "
                        + "JOIN FETCH p.user "
                        + "JOIN FETCH p.car c "
                        + "JOIN FETCH c.engine "
                        + "LEFT JOIN FETCH p.photos "
                        + "WHERE c.name = :carName "
                        + "ORDER BY p.created DESC",
                Post.class,
                Map.of("carName", carName)
        );
    }

}
