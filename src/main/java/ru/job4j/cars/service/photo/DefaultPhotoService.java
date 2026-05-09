package ru.job4j.cars.service.photo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.job4j.cars.dto.FileDto;
import ru.job4j.cars.model.Photo;
import ru.job4j.cars.repository.photo.PhotoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DefaultPhotoService implements PhotoService {

    private final PhotoRepository photoRepository;

    private final String storageDirectory;

    public DefaultPhotoService(PhotoRepository photoRepository,
                               @Value("${file.directory}") String storageDirectory) {
        this.photoRepository = photoRepository;
        this.storageDirectory = storageDirectory;
        createStorageDirectory(storageDirectory);
    }

    @Override
    public Photo save(FileDto fileDto) {
        var path = getNewFilePath(fileDto.getName());
        writeFileBytes(path, fileDto.getContent());
        var photo = new Photo();
        photo.setName(fileDto.getName());
        photo.setPath(path);
        return photoRepository.create(photo);
    }

    @Override
    public List<Photo> saveAll(List<FileDto> fileDtos) {
        return fileDtos.stream()
                .filter(fileDto -> fileDto.getContent().length > 0)
                .map(this::save)
                .toList();
    }

    @Override
    public Optional<FileDto> findById(Integer photoId) {
        var photoOptional = photoRepository.findById(photoId);
        if (photoOptional.isEmpty()) {
            return Optional.empty();
        }
        var photo = photoOptional.get();
        return Optional.of(new FileDto(photo.getName(), readFileBytes(photo.getPath())));
    }

    @Override
    public void delete(Integer photoId) {
        var photoOptional = photoRepository.findById(photoId);
        if (photoOptional.isPresent()) {
            deleteFile(photoOptional.get().getPath());
            photoRepository.delete(photoId);
        }
    }

    @Override
    public void deleteFiles(List<Photo> photos) {
        photos.forEach(photo -> deleteFile(photo.getPath()));
    }

    private void createStorageDirectory(String path) {
        try {
            Files.createDirectories(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getNewFilePath(String sourceName) {
        return storageDirectory
                + File.separator
                + UUID.randomUUID()
                + "_"
                + sourceName;
    }

    private void writeFileBytes(String path, byte[] content) {
        try {
            Files.write(Path.of(path), content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readFileBytes(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFile(String path) {
        try {
            Files.deleteIfExists(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
