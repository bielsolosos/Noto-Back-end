package space.bielsolososdev.noto.domain.media.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import space.bielsolososdev.noto.api.mapper.UserMapper;
import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.media.model.MediaR2;
import space.bielsolososdev.noto.domain.media.repository.MediaR2Repository;
import space.bielsolososdev.noto.domain.media.service.MediaService;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;
import space.bielsolososdev.noto.domain.users.service.UserService;
import space.bielsolososdev.noto.infrastructure.R2Properties;

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
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public MediaResponse upload(MediaRequest media) {
        return uploadForUser(media, userService.getMe());
    }

    @Override
    @Transactional
    public UserResponse uploadProfileImage(MediaRequest media) {
        User me = userService.getMe();
        MediaR2 previousProfileMedia = me.getProfileMedia();

        MediaResponse uploaded = uploadForUser(media, me);
        MediaR2 newProfileMedia = repository.findById(uploaded.id())
                .orElseThrow(() -> new BusinessException("Imagem recém-enviada não encontrada"));

        me.setProfileMedia(newProfileMedia);
        userRepository.save(me);

        if (previousProfileMedia != null) {
            deleteObject(previousProfileMedia.getFilename());
            repository.delete(previousProfileMedia);
        }

        return UserMapper.toUserResponse(me);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf(".");
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        User me = userService.getMe();

        MediaR2 media = repository.findById(id)
                .orElseThrow(() -> new BusinessException("Imagem não encontrada"));

        if (!me.equals(media.getUser())) {
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

    private MediaResponse uploadForUser(MediaRequest media, User user) {
        var file = media.file();

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
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String finalUrl = r2Properties.getPublicUrlBase() + "/" + key;
            String markdown = String.format("![%s](%s)", file.getOriginalFilename(), finalUrl);

            MediaR2 entity = repository.save(MediaR2.builder()
                    .url(finalUrl)
                    .user(user)
                    .createdAt(OffsetDateTime.now())
                    .filename(key)
                    .sizeBytes(file.getSize())
                    .contentType(file.getContentType())
                    .build());

            return new MediaResponse(entity.getId(), key, finalUrl, markdown, entity.getCreatedAt());

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar upload de imagem", e);
        }
    }

    private void deleteObject(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .build();

        s3Client.deleteObject(deleteRequest);
    }
}