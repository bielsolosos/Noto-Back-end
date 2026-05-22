package br.dev.bielsolosos.noto.api.mapper.page;

import lombok.experimental.UtilityClass;
import br.dev.bielsolosos.noto.api.model.page.PageSummaryResponse;
import br.dev.bielsolosos.noto.domain.pages.model.Page;

@UtilityClass
public class PageMapper {

    public PageSummaryResponse toSummary(Page page) {
        return new PageSummaryResponse(page.getId(), page.getTitle(), page.getUpdatedAt());
    }

    public PageResponse toPageResponse(Page page) {
        return new PageResponse(page.getId(), page.getTitle(), page.getContent(), page.getCreatedAt(), page.getUpdatedAt());
    }
}
