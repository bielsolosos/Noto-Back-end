package space.bielsolososdev.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import space.bielsolososdev.noto.api.annotations.IsAdmin;
import space.bielsolososdev.noto.api.mapper.UserMapper;
import space.bielsolososdev.noto.api.model.MessageResponse;
import space.bielsolososdev.noto.api.model.user.ChangePasswordRequest;
import space.bielsolososdev.noto.api.model.user.EditUserRequest;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.domain.users.repository.UserRepository;
import space.bielsolososdev.noto.domain.users.service.AdminUserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@IsAdmin
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService userService;
    //TODO remover
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Page<UserResponse>> listUsers(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable, @RequestParam(required = false) String filter, @RequestParam(required = false) Boolean isActive, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter, @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore) {
        Page<UserResponse> response = userService.listUsers(pageable, filter, isActive, createdAfter, createdBefore).map(UserMapper::toUserResponse);
        return ResponseEntity.ok(response);
    }

    //TODO remover
    @GetMapping("/list")
    public ResponseEntity<List<UserResponse>> listUsersToList() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(UserMapper::toUserResponse).toList());
    }

    @PatchMapping("/{id}/credentials")
    public ResponseEntity<UserResponse> editUser(@PathVariable UUID id, @Valid @RequestBody EditUserRequest request) {
        return ResponseEntity.ok(UserMapper.toUserResponse(userService.adminEditUser(id, request.username(), request.email())));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<MessageResponse> changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.adminChangePassword(id, request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso"));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<UserResponse> toggleActive(@PathVariable UUID id) {
        return ResponseEntity.ok(UserMapper.toUserResponse(userService.toggleUserActive(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("Usuário deletado com sucesso"));
    }
}

