package space.bielsolososdev.noto.api.controller.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.domain.media.model.MediaR2;
import space.bielsolososdev.noto.domain.media.service.MediaService;
import space.bielsolososdev.noto.domain.users.model.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private MediaController controller;

    private MediaR2 mediaR2;
    private UUID mediaId;

    @BeforeEach
    void setUp() {
        mediaId = UUID.randomUUID();

        User user = new User();
        user.setId(UUID.randomUUID());

        mediaR2 = new MediaR2();
        mediaR2.setId(mediaId);
        mediaR2.setFilename("image.png");
        mediaR2.setUrl("https://s3.example.com/image.png");
        mediaR2.setContentType("image/png");
        mediaR2.setSizeBytes(1024L);
        mediaR2.setUser(user);
        mediaR2.setCreatedAt(OffsetDateTime.now());
    }

    @Test
    void listAllMediaSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        String filter = "image";
        Page<MediaR2> page = new PageImpl<>(List.of(mediaR2));

        when(mediaService.listPageable(pageable, filter)).thenReturn(page);

        ResponseEntity<Page<MediaResponse>> response = controller.listAllMedia(pageable, filter);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals(mediaR2.getId(), response.getBody().getContent().get(0).id());
        assertEquals(mediaR2.getFilename(), response.getBody().getContent().get(0).fileName());
        verify(mediaService, times(1)).listPageable(pageable, filter);
    }

    @Test
    void addMediaSuccess() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "content".getBytes());
        MediaRequest request = new MediaRequest(file);
        
        when(mediaService.upload(file)).thenReturn(mediaR2);

        ResponseEntity<MediaResponse> response = controller.addMedia(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(mediaR2.getId(), response.getBody().id());
        assertEquals(mediaR2.getFilename(), response.getBody().fileName());
        verify(mediaService, times(1)).upload(file);
    }

    @Test
    void deleteMediaSuccess() {
        ResponseEntity<?> response = controller.deleteMedia(mediaId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(mediaService, times(1)).delete(mediaId);
    }
}
