package ru.find.me.profiles;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.find.me.UserService;
import ru.find.me.dao.ProfileRepo;
import ru.find.me.model.Profile;
import ru.find.me.publications.util.TransferFile;

import java.io.IOException;

@Controller
public class UpdateProfileController {

    private final UserService userService;

    private final ProfileRepo profileRepo;

    private final TransferFile transferFile;

    public UpdateProfileController(UserService userService, ProfileRepo repo, TransferFile transferFile) {
        this.userService = userService;
        this.profileRepo = repo;
        this.transferFile = transferFile;
    }

    @PostMapping("/update-profile")
    public String update(@RequestParam("id") long id,
                         @RequestParam("firstName") String firstName,
                         @RequestParam("lastName") String lastName,
                         @RequestParam("sex") String sex,
                         @RequestParam("age") Integer age,
                         @RequestParam("country") String country,
                         @RequestParam("city") String city,
                         @RequestParam("skills") String skills,
                         @RequestParam("phoneNumber") String phoneNumber,
                         @RequestParam("email") String email,
                         @RequestParam("description") String description,
                         @RequestParam("address") String address,
                         @RequestParam("file")MultipartFile file
                         ) throws IOException {
        Profile profile = profileRepo.findById(id).get();
        profile.setSex(sex);
        profile.setAge(age);
        profile.setCountry(country);
        profile.setSkills(skills);
        profile.setCity(city);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setPhoneNumber(phoneNumber);
        profile.setEmail(email);
        profile.setDescription(description);
        profile.setAddress(address);
        transferFile.transFile(file, profile);
        profileRepo.save(profile);
        return "redirect:/profile/" + profile.getUser().getId();
    }
}
