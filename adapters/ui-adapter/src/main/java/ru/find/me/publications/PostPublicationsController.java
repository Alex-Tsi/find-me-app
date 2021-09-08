package ru.find.me.publications;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.find.me.PublicationService;
import ru.find.me.model.Publication;
import ru.find.me.model.User;
import ru.find.me.util.TransferFile;

@Controller
@RequestMapping("/publications")
public class PostPublicationsController {

    private final TransferFile transferFile;

    private final PublicationService publicationService;

    public PostPublicationsController(
            @Qualifier("publicationServiceImpl") PublicationService publicationService,
            TransferFile transferFile
    ) {
        this.transferFile = transferFile;
        this.publicationService = publicationService;
    }

    /*@SneakyThrows
    @PostMapping("/create")
    public String createPublication(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "description") String description,
            @RequestParam(name = "tags") String tags,
            @RequestParam("file") MultipartFile file,
            Model model
    ) {
        var publication = new Publication(description, tags, user);
        transferFile.transFile(file, publication);
        System.out.println(publication.getTags());
        publicationService.save(publication);
        model.addAttribute("publications", publication);
        return "redirect:/publications/find";
    }*/

    @SneakyThrows
    @GetMapping("/create")
        public String createPublication(@AuthenticationPrincipal User user,
                Model model) {
        var publication = new Publication();
        publication.setAuthor(user);
        publicationService.save(publication);
        model.addAttribute("publication", publication);
        return "redirect:/edit-publication/" + publication.getId();
    }
}
