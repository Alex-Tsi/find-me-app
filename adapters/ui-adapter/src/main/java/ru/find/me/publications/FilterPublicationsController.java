package ru.find.me.publications;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String filterPublications(@RequestParam(name = "filter", required = false) String filter, Model model) {
        Iterable<Publication> publications = getPublications(filter);
        model.addAttribute("publications", publications);
        return "publications/publications";
    }

    @GetMapping("/filter/{tag}")
    public String filterPublication(@PathVariable("tag") String tag,
                                    Model model) {
        Iterable<Publication> publications = getPublications(tag);
        model.addAttribute("publications", publications);
        return "publications/publications";
    }

    private Iterable<Publication> getPublications(String filter) {
        Iterable<Publication> publications;
        if (filter != null && !filter.isEmpty()) {
            publications = publicationService.findByTags(filter);
        } else publications = publicationService.findAll();
        return publications;
    }
}
