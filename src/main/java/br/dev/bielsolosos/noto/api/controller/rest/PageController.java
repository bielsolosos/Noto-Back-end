package br.dev.bielsolosos.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;
import br.dev.bielsolosos.noto.api.mapper.page.PageMapper;
import br.dev.bielsolosos.noto.api.mapper.page.PageRequest;
import br.dev.bielsolosos.noto.api.mapper.page.PageResponse;
import br.dev.bielsolosos.noto.api.model.MessageResponse;
import br.dev.bielsolosos.noto.api.model.page.PageSummaryResponse;
import br.dev.bielsolosos.noto.core.enums.ExportTypeEnum;
import br.dev.bielsolosos.noto.core.ratelimit.IpRateLimiter;
import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.model.dto.PageToExportDto;
import br.dev.bielsolosos.noto.domain.pages.service.PageService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageService service;

    @GetMapping("/list")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<List<PageSummaryResponse>> listAllPagesForSummary(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "UPDATED_AT") PageSortByEnum sortBy,
            @RequestParam(defaultValue = "DESC") PageSortOrderEnum sortOrder) {
        return ResponseEntity.ok(service.getAll(query, sortBy, sortOrder).stream().map(PageMapper::toSummary).toList());
    }

    @GetMapping("/{id}")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<PageResponse> getPage(@PathVariable UUID id) {
        return ResponseEntity.ok(PageMapper.toPageResponse(service.getById(id)));
    }

    @DeleteMapping("/{id}")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<MessageResponse> deletePage(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(new MessageResponse("Página deletado com sucesso"));
    }

    @PutMapping("/{id}")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<PageResponse> editPageContent(@Valid @RequestBody PageRequest request,
            @PathVariable UUID id) {
        return ResponseEntity
                .ok(PageMapper.toPageResponse(service.updateContent(id, request.title(), request.content())));
    }

    @PostMapping
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageRequest request) {
        return ResponseEntity.ok(PageMapper.toPageResponse(service.createPage(request.title(), request.content())));
    }

    @GetMapping("/{id}/export")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<Resource> exportPage(@PathVariable UUID id, @RequestParam ExportTypeEnum type) {
        PageToExportDto pojo = service.exportPage(id, type);

        String encodedFileName = UriUtils.encode(pojo.fileName(), StandardCharsets.UTF_8);
        String contentDisposition = String.format(
                "attachment; filename=\"%s\"; filename*=UTF-8''%s",
                pojo.fileName().replace("\"", ""),
                encodedFileName);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .contentType(MediaType.parseMediaType(pojo.mimeType().getMimeType()))
                .contentLength(pojo.contentLength())
                .body(new ByteArrayResource(pojo.resource()));
    }
}
