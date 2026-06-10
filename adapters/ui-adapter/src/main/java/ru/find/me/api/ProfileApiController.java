package ru.find.me.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.find.me.ProfileService;
import ru.find.me.UserService;
import ru.find.me.api.dto.ProfileDto;
import ru.find.me.api.dto.ProfileRequest;
import ru.find.me.model.Profile;
import ru.find.me.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/profile")
public class ProfileApiController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final UserService userService;
    private final ProfileService profileService;

    public ProfileApiController(@Qualifier("userServiceImpl") UserService userService,
                               @Qualifier("profileServiceImpl") ProfileService profileService) {
        this.userService = userService;
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ProfileDto myProfile(@AuthenticationPrincipal User user) {
        return ApiMapper.toDto(requireProfile(user));
    }

    @GetMapping("/{userId}")
    public ProfileDto userProfile(@PathVariable long userId) {
        User user = userService.findById(userId);
        return ApiMapper.toDto(requireProfile(user));
    }

    @PutMapping("/me")
    public ProfileDto updateMyProfile(@AuthenticationPrincipal User user,
                                      @Valid @RequestBody ProfileRequest request) {
        Profile profile = user.getProfile();
        if (profile == null) {
            profile = new Profile();
            profile.setRegistrationDate(LocalDateTime.now().format(DATE_FORMAT));
            user.setProfile(profile);
        }
        apply(request, profile);
        profileService.saveProfile(profile);
        return ApiMapper.toDto(profile);
    }

    private Profile requireProfile(User user) {
        Profile profile = user.getProfile();
        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Профиль не заполнен");
        }
        return profile;
    }

    private void apply(ProfileRequest request, Profile profile) {
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setSex(request.sex());
        profile.setAge(request.age());
        profile.setCountry(request.country());
        profile.setCity(request.city());
        profile.setSkills(request.skills());
        profile.setPhoneNumber(request.phoneNumber());
        profile.setEmail(request.email());
        profile.setDescription(request.description());
        profile.setAddress(request.address());
        if (request.avatar() != null) {
            profile.setAvatar(request.avatar());
        }
    }
}
