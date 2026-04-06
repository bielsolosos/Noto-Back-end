package space.bielsolososdev.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.domain.media.service.MediaService;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<MediaResponse> addMedia(MediaRequest media) {
        System.out.println(media.file());
        return ResponseEntity.ok(mediaService.upload(media));
    }
}
