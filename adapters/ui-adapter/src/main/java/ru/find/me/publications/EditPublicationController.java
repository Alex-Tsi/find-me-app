package ru.find.me.publications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.find.me.PublicationService;
import ru.find.me.model.Publication;
import ru.find.me.util.TransferFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
public class EditPublicationController {

    private final PublicationService publicationService;

    private final TransferFile transferFile;

    @Autowired
    public EditPublicationController(PublicationService publicationService,
                                     TransferFile transferFile) {
        this.publicationService = publicationService;
        this.transferFile = transferFile;
    }

    @GetMapping("/edit-publication/{id}")
    public String redact(@PathVariable("id") long id,
                         Model model) {
        Publication publication = publicationService.findById(id);
        model.addAttribute("publication", publication);
        return "publications/editPublication";
    }

    @PostMapping("/update-publication")
    public String update(@RequestParam("id") long id,
                         @RequestParam("title") String title,
                         @RequestParam("tags") String tags,
                         @RequestParam("motivations") String motivation,
                         @RequestParam("description") String description,
                         @RequestParam("rewards") String rewards,
                         @RequestParam("whoNeed") String whoNeed,
                         @RequestParam("fileName") MultipartFile fileName,
                         Model model
                         ) throws IOException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();

        formatter.format(date);
        Publication publication = publicationService.findById(id);
        publication.setTitle(title);
        publication.setDate(formatter.format(date));
        publication.setTags(tags);
        publication.setMotivations(motivation);
        publication.setDescription(description);
        publication.setRewards(rewards);
        publication.setWhoNeed(whoNeed);
        transferFile.transFile(fileName, publication);
        publicationService.save(publication);
        return "redirect:/publications/browse/" + publication.getId();
    }
}
