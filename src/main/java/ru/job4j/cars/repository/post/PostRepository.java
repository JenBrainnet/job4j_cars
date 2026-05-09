package ru.job4j.cars.repository.post;

import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository {

    Post create(Post post);

    void update(Post post);

    void delete(Integer postId);

    List<Post> findAllOrderById();

    Optional<Post> findById(Integer postId);

    List<Post> findForLastDay();

    List<Post> findWithPhoto();

    List<Post> findByCarName(String carName);

}
