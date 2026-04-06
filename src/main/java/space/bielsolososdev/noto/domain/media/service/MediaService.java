package space.bielsolososdev.noto.domain.media.service;

import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;

import java.util.UUID;

public interface MediaService {

    MediaResponse upload(MediaRequest media);

    void delete(UUID id);
}
