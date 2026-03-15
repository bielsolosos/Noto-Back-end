package space.bielsolososdev.noto.api.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;
import space.bielsolososdev.noto.api.mapper.UserMapper;
import space.bielsolososdev.noto.api.model.MessageResponse;
import space.bielsolososdev.noto.api.model.user.ChangePasswordRequest;
import space.bielsolososdev.noto.api.model.user.CreateUserRequest;
import space.bielsolososdev.noto.api.model.user.EditUserRequest;
import space.bielsolososdev.noto.api.model.user.UserResponse;
import space.bielsolososdev.noto.core.exception.BusinessException;
import space.bielsolososdev.noto.domain.users.service.UserService;
import space.bielsolososdev.noto.infrastructure.NotoProperties;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;
    private final NotoProperties props;

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        service.changePassword(service.getMe().getId(), request.oldPassword(), request.newPassword());
        return ResponseEntity.ok(new MessageResponse("Senha alterada com sucesso"));
    }
    
    @PostMapping("/edit-credentials")
    public ResponseEntity<UserResponse> editUser(@Valid @RequestBody EditUserRequest request) {
        return ResponseEntity.ok(
                UserMapper.toUserResponse(
                        service.editUser(service.getMe().getId(), request.username(), request.email())
                )
        );
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request)  throws NoHandlerFoundException {
        if (!props.isRegistrationEnabled()) {
            throw new NoHandlerFoundException("POST", "/api/users/register", new org.springframework.http.HttpHeaders());
        }

        if (!request.password().equals(request.confirmPassword())) {
            throw new BusinessException("As senhas não conferem");
        }

        UserResponse userResponse = UserMapper.toUserResponse(
                service.createUser(request.username(), request.email(), request.password())
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }
}

