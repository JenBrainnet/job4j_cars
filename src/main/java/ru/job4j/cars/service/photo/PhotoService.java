package ru.job4j.cars.service.photo;

import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Photo;

import java.util.List;
import java.util.Optional;

public interface PhotoService {

    Photo save(FileDto fileDto);

    List<Photo> saveAll(List<FileDto> fileDtos);

    Optional<FileDto> findById(Integer photoId);

    void delete(Integer photoId);

    void deleteFiles(List<Photo> photos);

}
