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
