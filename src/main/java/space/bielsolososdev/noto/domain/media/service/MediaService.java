package space.bielsolososdev.noto.domain.media.service;

import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.api.model.user.UserResponse;

import java.util.UUID;

public interface MediaService {

    MediaResponse upload(MediaRequest media);

    UserResponse uploadProfileImage(MediaRequest media);

    void delete(UUID id);
}
