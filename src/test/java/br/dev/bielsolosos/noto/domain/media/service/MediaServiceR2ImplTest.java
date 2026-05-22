package br.dev.bielsolosos.noto.domain.media.service;

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
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;
import br.dev.bielsolosos.noto.domain.media.repository.MediaR2Repository;
import br.dev.bielsolosos.noto.domain.media.repository.specification.MediaSpecification;
import br.dev.bielsolosos.noto.domain.media.service.impl.MediaServiceR2Impl;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;
import br.dev.bielsolosos.noto.domain.users.service.MeService;
import br.dev.bielsolosos.noto.infrastructure.R2Properties;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaServiceR2ImplTest {

    @Mock
    private S3Client s3Client;
    @Mock
    private R2Properties r2Properties;
    @Mock
    private MediaR2Repository repository;
    @Mock
    private MeService meService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MediaServiceR2Impl mediaService;

    private User me;
    private User other;
    private MediaR2 media;
    private UUID mediaId;

    @BeforeEach
    void setUp() {
        me = new User();
        me.setId(UUID.randomUUID());
        
        other = new User();
        other.setId(UUID.randomUUID());

        mediaId = UUID.randomUUID();
        media = new MediaR2();
        media.setId(mediaId);
        media.setUser(me);
        media.setFilename("file.png");
        media.setUrl("http://s3.local/file.png");
    }

    @Test
    void upload_shouldCallUploadForUser() {
        MockMultipartFile file = new MockMultipartFile("file", "image.png", "image/png", "data".getBytes());
        when(meService.getMe()).thenReturn(me);
        when(r2Properties.getBucketName()).thenReturn("test-bucket");
        when(r2Properties.getPublicUrlBase()).thenReturn("http://s3.local");
        when(repository.save(any(MediaR2.class))).thenAnswer(i -> i.getArgument(0));

        MediaR2 result = mediaService.upload(file);

        assertNotNull(result);
        assertEquals(me, result.getUser());
        verify(meService, times(1)).getMe();
        verify(s3Client, times(1)).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void delete_shouldDeleteSuccessfully() {
        when(meService.getMe()).thenReturn(me);
        when(repository.findById(mediaId)).thenReturn(Optional.of(media));
        when(r2Properties.getBucketName()).thenReturn("test-bucket");

        mediaService.delete(mediaId);

        verify(repository, times(1)).delete(media);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void delete_shouldThrowIfNotFound() {
        when(meService.getMe()).thenReturn(me);
        when(repository.findById(mediaId)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> mediaService.delete(mediaId));
    }

    @Test
    void delete_shouldThrowIfNotOwner() {
        when(meService.getMe()).thenReturn(other);
        when(repository.findById(mediaId)).thenReturn(Optional.of(media));

        assertThrows(BusinessException.class, () -> mediaService.delete(mediaId));
    }

    @Test
    void delete_shouldRemoveProfileMediaIfMatches() {
        me.setProfileMedia(media);
        when(meService.getMe()).thenReturn(me);
        when(repository.findById(mediaId)).thenReturn(Optional.of(media));
        when(r2Properties.getBucketName()).thenReturn("test-bucket");

        mediaService.delete(mediaId);

        assertNull(me.getProfileMedia());
        verify(userRepository, times(1)).save(me);
        verify(repository, times(1)).delete(media);
        verify(s3Client, times(1)).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    void listPageable_shouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MediaR2> expectedPage = new PageImpl<>(List.of(media));
        when(repository.findAll(any(MediaSpecification.class), eq(pageable))).thenReturn(expectedPage);

        Page<MediaR2> result = mediaService.listPageable(pageable, "filter");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(repository, times(1)).findAll(any(MediaSpecification.class), eq(pageable));
    }

    @Test
    void uploadForUser_shouldThrowIfFileEmpty() {
        MockMultipartFile file = new MockMultipartFile("file", new byte[0]);
        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadForUser(file, me));
        assertEquals("Arquivo de imagem é obrigatório", ex.getMessage());
    }

    @Test
    void uploadForUser_shouldThrowIfNotAllowedType() {
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "data".getBytes());
        BusinessException ex = assertThrows(BusinessException.class, () -> mediaService.uploadForUser(file, me));
        assertEquals("Apenas imagens (JPEG, PNG, WEBP) são permitidas!", ex.getMessage());
    }

    @Test
    void getMedia_shouldReturnMedia() {
        when(repository.findById(mediaId)).thenReturn(Optional.of(media));
        MediaR2 result = mediaService.getMedia(mediaId);
        assertNotNull(result);
        assertEquals(mediaId, result.getId());
    }

    @Test
    void getMedia_shouldThrowIfNotFound() {
        when(repository.findById(mediaId)).thenReturn(Optional.empty());
        assertThrows(BusinessException.class, () -> mediaService.getMedia(mediaId));
    }
}
