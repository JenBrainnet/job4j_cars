package ru.job4j.cars.repository.photo;

import ru.job4j.cars.model.Photo;

import java.util.Optional;

public interface PhotoRepository {

    Photo create(Photo photo);

    Optional<Photo> findById(Integer photoId);

    void delete(Integer photoId);

}
