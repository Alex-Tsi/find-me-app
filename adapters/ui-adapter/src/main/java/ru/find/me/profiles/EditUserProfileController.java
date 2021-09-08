package ru.find.me.profiles;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.model.User;

@Controller
public class EditUserProfileController {

    @GetMapping("/redact-user-profile/{user}")
    public String editProfile(@PathVariable("user") User user,
                             Model model) {
        model.addAttribute("profile", user.getProfile());
        model.addAttribute("profileId", user.getProfile().getId());
        return "/profile/edit";
    }
}
