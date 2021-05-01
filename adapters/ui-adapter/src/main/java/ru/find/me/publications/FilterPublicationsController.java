package ru.find.me.publications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.find.me.PublicationService;
import ru.find.me.model.Publication;

@Controller
@RequestMapping("/publications")
public class FilterPublicationsController {

    private final PublicationService publicationService;

    public FilterPublicationsController(@Qualifier("publicationServiceImpl") PublicationService messageService) {
        this.publicationService = messageService;
    }

    @GetMapping("/filter")
    public String filterPublications(@RequestParam(name = "filter") String filter, Model model) {
        Iterable<Publication> publications;
        if (filter != null && !filter.isEmpty()) {
            publications = publicationService.findByTag(filter);
        } else publications = publicationService.findAll();
        model.addAttribute("publications", publications);
        return "publications/publications";
    }
}
