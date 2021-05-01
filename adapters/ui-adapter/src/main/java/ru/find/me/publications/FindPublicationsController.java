package ru.find.me.publications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.find.me.PublicationService;

@Controller
@RequestMapping("/publications")
public class FindPublicationsController {

    private final PublicationService publicationService;

    public FindPublicationsController(@Qualifier("publicationServiceImpl") PublicationService publicationService) {
        this.publicationService = publicationService;
    }

    @GetMapping("/find")
    public String findPublications(Model model) {
        var publications = publicationService.findAll();
        model.addAttribute("publications", publications);
        return "publications/publications";
    }
}
