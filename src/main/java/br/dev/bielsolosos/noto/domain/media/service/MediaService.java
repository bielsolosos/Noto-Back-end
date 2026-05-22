package br.dev.bielsolosos.noto.domain.media.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;
import br.dev.bielsolosos.noto.domain.users.model.User;

import java.util.UUID;

public interface MediaService {

    MediaR2 upload(MultipartFile media);

    void delete(UUID id);

    Page<MediaR2> listPageable(Pageable pageable, String filter);

    MediaR2 uploadForUser(MultipartFile media, User me);

    MediaR2 getMedia(UUID id);
}
