package br.dev.bielsolosos.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import br.dev.bielsolosos.noto.api.mapper.user.UserMapper;
import br.dev.bielsolosos.noto.api.model.MessageResponse;
import br.dev.bielsolosos.noto.api.model.media.MediaRequest;
import br.dev.bielsolosos.noto.api.model.user.ChangePasswordRequest;
import br.dev.bielsolosos.noto.api.model.user.CreateUserRequest;
import br.dev.bielsolosos.noto.api.model.user.EditUserRequest;
import br.dev.bielsolosos.noto.api.model.user.UserResponse;
import br.dev.bielsolosos.noto.core.exception.BusinessException;
import br.dev.bielsolosos.noto.core.ratelimit.IpRateLimiter;
import br.dev.bielsolosos.noto.core.ratelimit.enums.IpRateLimitConfigEnum;
import br.dev.bielsolosos.noto.domain.users.service.MeService;
import br.dev.bielsolosos.noto.domain.users.service.UserService;
import br.dev.bielsolosos.noto.infrastructure.NotoProperties;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final MeService meService;
    private final NotoProperties props;

    @PostMapping("/change-password")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(meService.getMe().getId(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso"));
    }

    @PostMapping("/edit-credentials")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<UserResponse> editUser(@Valid @RequestBody EditUserRequest request) {
        return ResponseEntity.ok(
                UserMapper.toUserResponse(
                        service.editUser(meService.getMe().getId(), request.username(), request.email())));
    }

    @PostMapping("/profile-image")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<UserResponse> uploadProfileImage(@ModelAttribute MediaRequest mediaRequest) {
        return ResponseEntity.ok(UserMapper.toUserResponse(service.uploadProfileImage(mediaRequest.file())));
    }

    @PostMapping("/register")
    @IpRateLimiter(IpRateLimitConfigEnum.PRIVATE_ROUTES)
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)
            throws NoHandlerFoundException {
        if (!props.isRegistrationEnabled()) {
            throw new NoHandlerFoundException("POST", "/api/users/register",
                    new org.springframework.http.HttpHeaders());
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException("As senhas não conferem");
        }

        UserResponse userResponse = UserMapper.toUserResponse(
                service.createUser(request.username(), request.email(), request.password()));

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}
