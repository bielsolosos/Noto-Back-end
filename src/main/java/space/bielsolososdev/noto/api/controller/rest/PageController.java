package space.bielsolososdev.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.bielsolososdev.noto.api.mapper.PageMapper;
import space.bielsolososdev.noto.api.mapper.PageRequest;
import space.bielsolososdev.noto.api.mapper.PageResponse;
import space.bielsolososdev.noto.api.model.MessageResponse;
import space.bielsolososdev.noto.api.model.page.PageSummaryResponse;
import space.bielsolososdev.noto.domain.pages.service.PageService;

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
}
