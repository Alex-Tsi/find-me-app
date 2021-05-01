package ru.find.me.publications;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.find.me.PublicationService;
import ru.find.me.model.Publication;
import ru.find.me.model.User;

import java.io.File;
import java.util.UUID;

@Controller
@RequestMapping("/publications")
public class PostPublicationsController {

    @Value("${upload.path}")
    private String uploadPath;

    private final PublicationService publicationService;

    public PostPublicationsController(
            @Qualifier("publicationServiceImpl") PublicationService publicationService
    ) {
        this.publicationService = publicationService;
    }

    @SneakyThrows
    @PostMapping("/create")
    public String createPublication(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "tag") String tag,
            @RequestParam("file") MultipartFile file,
            Model model
    ) {
        var publication = new Publication(description, tag, user);
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadFile = new File(uploadPath);
            if (!uploadFile.exists()) uploadFile.mkdir();

            String uuid = UUID.randomUUID().toString();
            String resultFileName = uuid + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));
            publication.setFileName(resultFileName);
        }

        publicationService.save(publication);
        model.addAttribute("publications", publication);
        return "redirect:/publications/find";
    }
}
