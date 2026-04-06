package space.bielsolososdev.noto.domain.media.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import space.bielsolososdev.noto.api.model.media.MediaRequest;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.domain.media.service.MediaService;
import space.bielsolososdev.noto.infrastructure.R2Properties;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MediaServiceR2Impl implements MediaService {

    private final S3Client s3Client;
    private final R2Properties r2Properties;
    // private final MediaRepository mediaRepository; // Injete seu repositório aqui

    @Override
    public MediaResponse upload(MediaRequest media) {
        var file = media.file();
        // 1. Gerar nome único para evitar sobrescrever arquivos
        String extension = getFileExtension(file.getOriginalFilename());
        String key = UUID.randomUUID() + extension;

        try {
            // 2. Preparar o Upload para o R2
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            // 3. Enviar o arquivo
            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            // 4. Montar a URL e o Markdown
            String finalUrl = r2Properties.getPublicUrlBase() + "/" + key;
            String markdown = String.format("![%s](%s)", file.getOriginalFilename(), finalUrl);

            // 5. Salvar no Banco (Metadados)
            // Aqui você chamaria o repository.save(...) usando o media.userId()

            return new MediaResponse(
                    UUID.randomUUID(), // Trocar pelo ID gerado pelo banco após o save
                    key,
                    finalUrl,
                    markdown,
                    OffsetDateTime.now()
            );

        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar upload de imagem", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null) return "";
        int lastIndex = fileName.lastIndexOf(".");
        return (lastIndex == -1) ? "" : fileName.substring(lastIndex);
    }
}