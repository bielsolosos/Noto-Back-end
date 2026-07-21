package br.dev.bielsolosos.noto.domain.media.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.media.model.MediaR2;
import br.dev.bielsolosos.noto.domain.media.repository.MediaR2Repository;
import br.dev.bielsolosos.noto.domain.media.repository.specification.MediaSpecification;
import br.dev.bielsolosos.noto.domain.media.service.MediaService;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.repository.UserRepository;
import br.dev.bielsolosos.noto.domain.users.service.MeService;
import br.dev.bielsolosos.noto.infrastructure.R2Properties;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceR2Impl implements MediaService {

    private final S3Client s3Client;
    private final R2Properties r2Properties;
    private final MediaR2Repository repository;
    private final MeService meService;
    private final UserRepository userRepository;

    @Override
    public MediaR2 upload(MultipartFile media) {
        return uploadForUser(media, meService.getMe());
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf(".");
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User me = meService.getMe();

        MediaR2 media = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Imagem não encontrada"));

        if (!media.getUser().getId().equals(me.getId())) {
            throw new BusinessException("Você não tem permissão para deletar.");
        }

        if (me.getProfileMedia() != null && id.equals(me.getProfileMedia().getId())) {
            me.setProfileMedia(null);
            userRepository.save(me);
        }

        try {
            deleteObject(media.getFilename());

            repository.delete(media);

        } catch (S3Exception e) {
            throw new BusinessException("Erro ao deletar arquivo no R2", e);
        }
    }

    @Override
    public Page<MediaR2> listPageable(Pageable pageable, String filter) {
        User me = meService.getMe();
        MediaSpecification specification = new MediaSpecification(me.getId(), filter);
        return repository.findAll(specification, pageable);
    }

    @Override
    public MediaR2 uploadForUser(MultipartFile file, User user) {
        List<String> allowedTypes = Arrays.asList("image/jpeg", "image/png", "image/webp");

        if (file == null || file.isEmpty()) {
            throw new BusinessException("Arquivo de imagem é obrigatório");
        }

        if (!allowedTypes.contains(file.getContentType())) {
            throw new BusinessException("Apenas imagens (JPEG, PNG, WEBP) são permitidas!");
        }

        String extension = getFileExtension(file.getOriginalFilename());
        String key = UUID.randomUUID() + extension;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(r2Properties.getBucketName()).key(key).contentType(file.getContentType()).build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String finalUrl = r2Properties.getPublicUrlBase() + "/" + key;

            return repository.save(MediaR2.builder().url(finalUrl).user(user).createdAt(OffsetDateTime.now()).filename(key).sizeBytes(file.getSize()).contentType(file.getContentType()).build());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar upload de imagem", e);
        }
    }

    @Override
    public MediaR2 getMedia(UUID id) {
        User me = meService.getMe();
        MediaR2 media = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Media não encontrada"));

        if (!media.getUser().getId().equals(me.getId())) {
            throw new BusinessException("Você não tem permissão para acessar essa mídia.");
        }

        return media;
    }

    private void deleteObject(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}