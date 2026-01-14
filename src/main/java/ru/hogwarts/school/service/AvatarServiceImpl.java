package ru.hogwarts.school.service;


import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AvatarServiceImpl implements AvatarService {

    private static final Logger logger = LoggerFactory.getLogger(AvatarServiceImpl.class);

    @Value("${avatars.dir.path}")
    private String avatarsDir;

    private final StudentRepository studentRepository;
    private final AvatarRepository avatarRepository;

    public AvatarServiceImpl(StudentRepository studentRepository, AvatarRepository avatarRepository) {
        this.studentRepository = studentRepository;
        this.avatarRepository = avatarRepository;
    }


    public Avatar findAvatar(Long studentId) {
        logger.info("Was invoked method to find avatar for student id={}", studentId);
        Avatar avatar = avatarRepository.findByStudentId(studentId)
                .orElse(new Avatar());
        if (avatar.getId() == null) {
            logger.warn("No avatar found for student id={}", studentId);
        } else {
            logger.debug("Avatar found: id={}, filePath={}", avatar.getId(), avatar.getFilePath());
        }
        return avatar;
    }

    @Override
    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for upload avatar for student with id={}", studentId);

        if (file.isEmpty()) {
            logger.error("File is empty for student id={}", studentId);
            throw new IllegalArgumentException("Файл не выбран");
        }
        if (file.getSize() > 1024 * 300) {
            logger.warn("File size exceeds limit (300KB) for student id={}, size={} bytes", studentId, file.getSize());
            throw new IllegalArgumentException("Файл слишком большой");
        }
        if (file.getContentType() == null) {
            logger.error("Content type is null for file of student id={}", studentId);
            throw new IllegalArgumentException("Тип файла не указан");
        }

        Optional<Student> studentOpt = studentRepository.findById(studentId);
        if (studentOpt.isEmpty()) {
            logger.error("There is no student with id={}", studentId);
            throw new IllegalArgumentException("Студент не найден: " + studentId);
        }
        Student student = studentOpt.get();
        logger.debug("Found student: id={}, name={}", student.getId(), student.getName());

        String extension = getExtension(file.getOriginalFilename());
        Path filePath = Path.of(avatarsDir, studentId + "." + extension);

        if (!Files.exists(filePath.getParent())) {
            Files.createDirectories(filePath.getParent());
            logger.debug("Created directory for avatars: {}", filePath.getParent());
        }

        Optional<Avatar> existingAvatar = avatarRepository.findByStudentId(studentId);
        if (existingAvatar.isPresent()) {
            Files.deleteIfExists(Path.of(existingAvatar.get().getFilePath()));
            logger.debug("Deleted old avatar file for student id={}", studentId);
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
        logger.info("Avatar successfully uploaded for student id={}", studentId);
    }


    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public Page<Avatar> getAllAvatars(Pageable pageable) {
        logger.info("Was invoked method to get all avatars with pageable={}", pageable);
        Page<Avatar> avatars = avatarRepository.findAll(pageable);
        logger.debug("Retrieved {} avatars for page {}", avatars.getTotalElements(), pageable.getPageNumber());
        return avatars;
    }
}
