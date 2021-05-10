package ru.find.me.publications;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.PublicationService;

@Controller
public class DeletePublicationController {

    private final PublicationService service;

    public DeletePublicationController(PublicationService service) {
        this.service = service;
    }

    @GetMapping("/delete-publication/{id}")
    public String deletePublication(@PathVariable("id") long id) {
        service.deletePublication(id);
        return "redirect:/publications/find";
    }
}
