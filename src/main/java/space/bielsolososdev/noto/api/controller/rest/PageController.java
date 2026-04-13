package space.bielsolososdev.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.bielsolososdev.noto.api.mapper.page.PageMapper;
import space.bielsolososdev.noto.api.mapper.page.PageRequest;
import space.bielsolososdev.noto.api.mapper.page.PageResponse;
import space.bielsolososdev.noto.api.model.MessageResponse;
import space.bielsolososdev.noto.api.model.page.PageSummaryResponse;
import space.bielsolososdev.noto.core.enums.ExportTypeEnum;
import space.bielsolososdev.noto.domain.pages.model.dto.PageToExportDto;
import space.bielsolososdev.noto.domain.pages.service.PageService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pages")
@RequiredArgsConstructor
public class PageController {
    private final PageService service;

    @GetMapping("/list")
    public ResponseEntity<List<PageSummaryResponse>> listAllPagesForSummary() {
        return ResponseEntity.ok(service.getAll().stream().map(PageMapper::toSummary).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageResponse> getPage(@PathVariable UUID id) {
        return ResponseEntity.ok(PageMapper.toPageResponse(service.getById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deletePage(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(new MessageResponse("Página deletado com sucesso"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PageResponse> editPageContent(@Valid @RequestBody PageRequest request, @PathVariable UUID id) {
        return ResponseEntity.ok(PageMapper.toPageResponse(service.updateContent(id, request.title(), request.content())));
    }

    @PostMapping
    public ResponseEntity<PageResponse> createPage(@Valid @RequestBody PageRequest request) {
        return ResponseEntity.ok(PageMapper.toPageResponse(service.createPage(request.title(), request.content())));
    }

    @GetMapping("/{id}/export")
    public ResponseEntity<Resource> exportPage(@PathVariable UUID id, @RequestParam ExportTypeEnum type) {
        PageToExportDto pojo = service.exportPage(id, type);

        ContentDisposition contentDisposition = ContentDisposition.builder("attachment")
                .filename(pojo.fileName(), StandardCharsets.UTF_8)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.parseMediaType(pojo.mimeType().getMimeType()))
                .contentLength(pojo.contentLength())
                .body(new ByteArrayResource(pojo.resource()));
    }
}

