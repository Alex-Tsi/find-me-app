package ru.find.me.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.find.me.api.dto.UploadResponse;
import ru.find.me.util.TransferFile;

import java.io.IOException;

/**
 * Загрузка файлов (аватары, файлы публикаций). Возвращает имя файла,
 * по которому он потом доступен как {@code /img/{fileName}}.
 */
@RestController
public class UploadApiController {

    private final TransferFile transferFile;

    public UploadApiController(TransferFile transferFile) {
        this.transferFile = transferFile;
    }

    @PostMapping("/api/upload")
    public UploadResponse upload(@RequestParam("file") MultipartFile file) throws IOException {
        return new UploadResponse(transferFile.store(file));
    }
}
