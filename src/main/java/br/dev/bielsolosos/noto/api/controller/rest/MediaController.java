package br.dev.bielsolosos.noto.api.controller.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.dev.bielsolosos.noto.api.mapper.page.MediaMapper;
import br.dev.bielsolosos.noto.api.model.media.MediaRequest;
import br.dev.bielsolosos.noto.api.model.media.MediaResponse;
import br.dev.bielsolosos.noto.domain.media.service.MediaService;

import java.util.UUID;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping
    public ResponseEntity<Page<MediaResponse>> listAllMedia(Pageable pageable,@RequestParam(required = false) String filter) {
        Page<MediaResponse> response = mediaService.listPageable(pageable, filter).map(MediaMapper::toResponse);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<MediaResponse> addMedia(MediaRequest media) {
        return ResponseEntity.ok(MediaMapper.toResponse(mediaService.upload(media.file())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMedia(@PathVariable UUID id) {
        mediaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
