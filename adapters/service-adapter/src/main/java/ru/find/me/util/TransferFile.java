package ru.find.me.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.find.me.model.Profile;
import ru.find.me.model.Publication;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class TransferFile {

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Сохраняет файл в {@code upload.path} под уникальным именем и возвращает это имя.
     * Используется REST-слоем (загрузка отделена от сущностей).
     *
     * @return имя сохранённого файла или {@code null}, если файл пуст
     */
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
            return null;
        }
        File uploadFile = new File(uploadPath);
        if (!uploadFile.exists()) uploadFile.mkdir();

        String resultFileName = UUID.randomUUID() + "." + file.getOriginalFilename();
        file.transferTo(new File(uploadPath + "/" + resultFileName));
        return resultFileName;
    }

    public void transFile(MultipartFile fileName, Publication publication) throws IOException {
        if (fileName != null && !fileName.getOriginalFilename().isEmpty()) {
            File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) uploadFile.mkdir();

            String uuid = UUID.randomUUID().toString();
            String resultFileName = uuid + "." + fileName.getOriginalFilename();
            fileName.transferTo(new File(uploadPath + "/" + resultFileName));
            publication.setFileName(resultFileName);
        }
    }

    public void transFile(MultipartFile fileName, Profile profile) throws IOException {
        if (fileName != null && !fileName.getOriginalFilename().isEmpty()) {
            File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) uploadFile.mkdir();

            String uuid = UUID.randomUUID().toString();
            String resultFileName = uuid + "." + fileName.getOriginalFilename();
            fileName.transferTo(new File(uploadPath + "/" + resultFileName));
            profile.setAvatar(resultFileName);
        }
    }
}
