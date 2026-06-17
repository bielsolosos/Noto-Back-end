package br.dev.bielsolosos.noto.domain.pages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import br.dev.bielsolosos.noto.core.enums.ExportTypeEnum;
import br.dev.bielsolosos.noto.core.enums.MimeTypeEnum;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortByEnum;
import br.dev.bielsolosos.noto.domain.pages.enums.PageSortOrderEnum;
import br.dev.bielsolosos.noto.domain.pages.export.factory.ExportPageFactory;
import br.dev.bielsolosos.noto.domain.pages.export.service.PageExporterService;
import br.dev.bielsolosos.noto.domain.pages.model.Page;
import br.dev.bielsolosos.noto.domain.pages.model.dto.PageToExportDto;
import br.dev.bielsolosos.noto.domain.pages.repository.PageRepository;
import br.dev.bielsolosos.noto.domain.pages.repository.specification.PageSpecification;
import br.dev.bielsolosos.noto.domain.users.model.User;
import br.dev.bielsolosos.noto.domain.users.service.MeService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PageService {

    private final MeService meService;
    private final PageRepository repository;

    public Page getById(UUID id) {
        Page entity = findById(id);
        validatePermission(id, entity);
        return entity;
    }

    private Page findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new BusinessException("Página não encontrada no sistema."));
    }

    public Page createPage(String title, String content) {
        Page page = new Page();
        page.setUser(meService.getMe());
        page.setTitle(
                title == null ? String.format("Nova Nota dia: %s", OffsetDateTime.now().toLocalDateTime().toString())
                        : title);
        page.setContent(content == null ? "" : content);
        return repository.save(page);
    }

    public Page updateContent(UUID id, String title, String content) {
        Page entity = getById(id);
        validatePermission(id, entity);

        entity.setContent(content);

        if (!entity.getTitle().equals(title)) {
            entity.setTitle(title);
        }

        return repository.save(entity);
    }

    public List<Page> getAll() {
        return getAll(null, PageSortByEnum.UPDATED_AT, PageSortOrderEnum.DESC);
    }

    public List<Page> getAll(String query, PageSortByEnum sortBy, PageSortOrderEnum sortOrder) {
        UUID userId = meService.getMe().getId();
        PageSpecification spec = new PageSpecification(userId, query, sortBy, sortOrder);
        return repository.findAll(spec);
    }

    public List<Page> getAllArchivedPages() {
        return repository.findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(meService.getMe().getId());
    }

    // TODO Add

    public void archivePage(UUID id) {
        Page entity = getById(id);
        entity.setArchived(true);
        entity.setArchivedAt(OffsetDateTime.now());
        repository.save(entity);
    }
    // TODO Add

    public void unarchivePage(UUID id) {
        Page entity = getById(id);
        entity.setArchived(false);
        entity.setArchivedAt(OffsetDateTime.now());
        repository.save(entity);
    }

    public void delete(UUID id) {
        User me = meService.getMe();
        Page entity = findById(id);
        validatePermission(entity, me);
        repository.delete(entity);
    }

    private void validatePermission(UUID id, Page entity) {
        User me = meService.getMe();
        if (!entity.getUser().getId().equals(me.getId())) {
            throw new BusinessException(
                    String.format("Usuário %s não tem permissão para editar a página de Id: %s", me.getUsername(), id));
        }
    }

    private void validatePermission(Page entity, User me) {
        if (!entity.getUser().getId().equals(me.getId())) {
            throw new BusinessException(
                    String.format("Usuário %s não tem permissão para editar a página de Id", me.getUsername()));
        }
    }

    public PageToExportDto exportPage(UUID id, ExportTypeEnum type) {
        Page entity = this.getById(id);

        ExportPageFactory factory = new ExportPageFactory();

        PageExporterService pageExporterService = factory.generatePageService(type);

        byte[] fileBytes = pageExporterService.getBytesFromPage(entity);

        String fileName = pageExporterService.getFileName(entity);

        MimeTypeEnum mimeType = pageExporterService.getMimeType();

        return new PageToExportDto(fileName, mimeType, fileBytes.length, fileBytes);
    }
}
