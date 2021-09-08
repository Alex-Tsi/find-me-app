package ru.find.me;

import ru.find.me.model.Profile;

public interface ProfileService {

    void saveProfile(Profile profile);

    Profile findById(long id);
}
