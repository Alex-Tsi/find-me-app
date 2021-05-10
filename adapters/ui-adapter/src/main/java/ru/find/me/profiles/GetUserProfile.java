package ru.find.me.profiles;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.find.me.UserService;
import ru.find.me.model.Profile;
import ru.find.me.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;


@Controller
public class GetUserProfile {

    private final UserService userService;

    public GetUserProfile(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{id}")
    public String getProfile(@PathVariable("id") User user,
                             Model model) {
        checkForProfile(user);
        model.addAttribute("profile", user.getProfile());
        return "profile/profile";
    }

    public void checkForProfile(User user) {
        if (user.getProfile() == null) {
            var profile = new Profile();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();
            profile.setRegistrationDate(formatter.format(date));
            user.setProfile(profile);
            userService.save(user);
        }
    }
}
