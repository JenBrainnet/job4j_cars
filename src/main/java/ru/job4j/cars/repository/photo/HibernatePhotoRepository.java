package ru.job4j.cars.repository.photo;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.command.CrudRepository;

import java.util.Map;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class HibernatePhotoRepository implements PhotoRepository {

    private final CrudRepository crudRepository;

    @Override
    public Photo create(Photo photo) {
        crudRepository.run(session -> session.persist(photo));
        return photo;
    }

    @Override
    public Optional<Photo> findById(Integer photoId) {
        return crudRepository.optional(
                "FROM Photo WHERE id = :id",
                Photo.class,
                Map.of("id", photoId)
        );
    }

    @Override
    public void delete(Integer photoId) {
        crudRepository.run(
                "DELETE FROM Photo WHERE id = :id",
                Map.of("id", photoId)
        );
    }

}
