package ru.find.me.publications;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.PublicationService;
import ru.find.me.model.Publication;

import java.util.*;

@Controller
public class BrowsePublicationController {

    private final PublicationService service;

    public BrowsePublicationController(PublicationService service) {
        this.service = service;
    }

    @GetMapping("/publications/browse/{publicationId}")
    public String brows(@PathVariable("publicationId") Long id,
                        Model model) {
        Publication publication = service.findById(id);
        addModel(model, publication);
        return "publications/browsePublication";
    }

    public List<String> extractMergedText(String mergedText) {
        String[] tags = Objects.requireNonNullElseGet(mergedText.split(" "), null);
        System.out.println(Arrays.toString(tags));
        return new ArrayList<>(Arrays.asList(tags));
    }

    private void addModel(Model model, Publication publication) {
        model.addAttribute("motivations", publication.getMotivations());
        model.addAttribute("descriptions", publication.getDescription());
        model.addAttribute("tags", extractMergedText(Objects.requireNonNullElse(publication.getTags(), "")));
        model.addAttribute("helpers", extractMergedText(Objects.requireNonNullElse(publication.getWhoNeed(), "")));
        model.addAttribute("publication", publication);
    }
}
