package ru.find.me.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.find.me.UserService;
import ru.find.me.model.Profile;
import ru.find.me.model.Role;
import ru.find.me.model.User;

import java.util.Collections;

@Controller
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(@Qualifier("userServiceImpl") UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/registration")
    public String registration() {
        return "/security/registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, Model model) {
        if (checkForExist(user)) {
            model.addAttribute("error", "Такой пользователь уже существует!");
            return "security/registration";
        } else createUser(user);
        return "redirect:/login";
    }

    public boolean checkForExist(User user) {
        return userService.findByUsername(user.getUsername()) != null;
    }

    public void createUser(User user) {
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        Profile profile = new Profile();
        user.setProfile(profile);
        userService.save(user);
    }
}
