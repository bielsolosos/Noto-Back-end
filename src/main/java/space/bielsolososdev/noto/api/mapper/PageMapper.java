package space.bielsolososdev.noto.api.mapper;

import lombok.experimental.UtilityClass;
import space.bielsolososdev.noto.api.model.page.PageSummaryResponse;
import space.bielsolososdev.noto.domain.pages.model.Page;

@UtilityClass
public class PageMapper {

    public PageSummaryResponse toSummary(Page page) {
        return new PageSummaryResponse(page.getId(), page.getTitle(), page.getUpdatedAt());
    }

    public PageResponse toPageResponse(Page page) {
        return new PageResponse(page.getId(), page.getTitle(), page.getContent(), page.getCreatedAt(), page.getUpdatedAt());
    }
}
