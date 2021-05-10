package ru.find.me.publications;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.model.Publication;
import ru.find.me.model.User;

import java.util.Set;

@Controller
public class UserPublicationsController {

    @GetMapping("/user-publications/{user}")
    public String userPublication(@AuthenticationPrincipal User currentUser,
                                  @PathVariable User user,
                                  Model model) {
        Set<Publication> publications = user.getPublications();
        model.addAttribute("publications", publications);
        model.addAttribute("isCurrentUser", currentUser.equals(user));

        return "/publications/userPublications";
    }
}
