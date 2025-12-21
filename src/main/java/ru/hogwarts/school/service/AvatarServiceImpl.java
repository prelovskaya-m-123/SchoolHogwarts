package ru.hogwarts.school.service;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;
import static java.nio.file.StandardOpenOption.CREATE_NEW;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


@Service
@Transactional
public class AvatarServiceImpl implements AvatarService{

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public AvatarServiceImpl(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }


    public Avatar findAvatar(Long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    @Override
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Файл не выбран");
        }
        if (file.getSize() > 1024 * 300) {
            throw new IllegalArgumentException("Файл слишком большой");
        }
        if (file.getContentType() == null) {
            throw new IllegalArgumentException("Тип файла не указан");
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            throw new IllegalArgumentException("Студент не найден: " + studentId);
        }
        Student student = studentOpt.get();

        String extension = getExtension(file.getOriginalFilename());
        Path filePath = Path.of(avatarsDir, studentId + "." + extension);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
        }

        Optional<Avatar> existingAvatar = avatarRepository.findByStudentId(studentId);
        if (existingAvatar.isPresent()) {
            Files.deleteIfExists(Path.of(existingAvatar.get().getFilePath()));
        }

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW)) {
            is.transferTo(os);
        }

        Avatar avatar = existingAvatar.orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toAbsolutePath().toString().replace("\\", "/"));
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }


    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
    @Override
    public Page<Avatar> getAllAvatars(Pageable pageable) {
        return avatarRepository.findAll(pageable);
    }

}
