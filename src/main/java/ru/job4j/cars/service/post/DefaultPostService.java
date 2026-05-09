package ru.job4j.cars.service.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Post;
import ru.job4j.cars.repository.post.PostRepository;
import ru.job4j.cars.service.photo.PhotoService;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class DefaultPostService implements PostService {

    private final PostRepository postRepository;

    private final PhotoService photoService;

    @Override
    public Post create(Post post, List<FileDto> photos) {
        post.getPhotos().addAll(photoService.saveAll(photos));
        return postRepository.create(post);
    }

    @Override
    public void update(Post post) {
        postRepository.update(post);
    }

    @Override
    public void delete(Integer postId) {
        var postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            photoService.deleteFiles(postOptional.get().getPhotos());
            postRepository.delete(postId);
        }
    }

    @Override
    public List<Post> findAllOrderById() {
        return postRepository.findAllOrderById();
    }

    @Override
    public Optional<Post> findById(Integer postId) {
        return postRepository.findById(postId);
    }

    @Override
    public List<Post> findForLastDay() {
        return postRepository.findForLastDay();
    }

    @Override
    public List<Post> findWithPhoto() {
        return postRepository.findWithPhoto();
    }

    @Override
    public List<Post> findByCarName(String carName) {
        return postRepository.findByCarName(carName);
    }

}
