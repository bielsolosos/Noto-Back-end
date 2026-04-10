package space.bielsolososdev.noto.api.mapper.page;

import lombok.experimental.UtilityClass;
import space.bielsolososdev.noto.api.model.media.MediaResponse;
import space.bielsolososdev.noto.domain.media.model.MediaR2;

@UtilityClass
public class MediaMapper {

    public MediaResponse toResponse(MediaR2 entity) {
        String markdown = String.format("![%s](%s)", entity.getFilename(), entity.getUrl());
        return new MediaResponse(entity.getId(), entity.getFilename(), entity.getUrl(), markdown, entity.getCreatedAt());
    }
}
