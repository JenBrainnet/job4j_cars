package ru.job4j.cars.service.post;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Post;

import java.util.List;
import java.util.Optional;

public interface PostService {

    Post create(Post post, List<FileDto> photos);

    void update(Post post);

    void delete(Integer postId);

    List<Post> findAllOrderById();

    Optional<Post> findById(Integer postId);

    List<Post> findForLastDay();

    List<Post> findWithPhoto();

    List<Post> findByCarName(String carName);

}
