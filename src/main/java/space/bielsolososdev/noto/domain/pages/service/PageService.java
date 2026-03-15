package space.bielsolososdev.noto.domain.pages.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.pages.model.Page;
import space.bielsolososdev.noto.domain.pages.repository.PageRepository;
import space.bielsolososdev.noto.domain.users.model.User;
import space.bielsolososdev.noto.domain.users.service.UserService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PageService {

    private final UserService userService;
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
        page.setUser(userService.getMe());
        page.setTitle(title == null ? String.format("Nova Nota dia: %s", OffsetDateTime.now().toLocalDateTime().toString()) : title);
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
        return repository.findByUserIdOrderByUpdatedAtDesc(userService.getMe().getId());
    }

    public List<Page> getAllArchivedPages() {
        return repository.findByUserIdAndArchivedTrueOrderByUpdatedAtDesc(userService.getMe().getId());
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
        User me = userService.getMe();
        Page entity = findById(id);
        validatePermission(entity, me);
        repository.delete(entity);
    }


    private void validatePermission(UUID id, Page entity) {
        User me = userService.getMe();
        if (!entity.getUser().getId().equals(me.getId())) {
            throw new BusinessException(String.format("Usuário %s não tem permissão para editar a página de Id: %s", me.getUsername(), id));
        }
    }

    private void validatePermission(Page entity, User me) {
        if (!entity.getUser().getId().equals(me.getId())) {
            throw new BusinessException(String.format("Usuário %s não tem permissão para editar a página de Id", me.getUsername()));
        }
    }
}
