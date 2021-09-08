package ru.find.me;

import org.springframework.stereotype.Service;
import ru.find.me.dao.ProfileRepo;
import ru.find.me.model.Profile;

@Service
public class ProfileServiceImpl implements ProfileService{

    private final ProfileRepo profileRepo;

    public ProfileServiceImpl(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    @Override
    public void saveProfile(Profile profile) {
        profileRepo.save(profile);
    }

    @Override
    public Profile findById(long id) {
        return profileRepo.findById(id).orElseGet(() -> {throw new RuntimeException("not found");});
    }
}
