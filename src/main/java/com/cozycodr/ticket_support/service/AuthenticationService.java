package com.cozycodr.ticket_support.service;

import com.cozycodr.ticket_support.exception.ResourceNotFoundException;
import com.cozycodr.ticket_support.exception.UserAlreadyExistsException;
import com.cozycodr.ticket_support.helpers.ApiResponseBody;
import com.cozycodr.ticket_support.model.dto.auth.AuthDataResponse;
import com.cozycodr.ticket_support.model.dto.auth.LoginRequest;
import com.cozycodr.ticket_support.model.dto.auth.RegistrationRequest;
import com.cozycodr.ticket_support.model.entity.User;
import com.cozycodr.ticket_support.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.cozycodr.ticket_support.helpers.ResponseHelpers.buildSuccessResponse;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public ResponseEntity<ApiResponseBody<AuthDataResponse>> register(RegistrationRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken((UserDetails) user);
        var data = AuthDataResponse.builder()
                .token(token)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();

        return buildSuccessResponse(HttpStatus.CREATED, "User Registered", data);

    }

    public ResponseEntity<ApiResponseBody<AuthDataResponse>> login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var token = jwtService.generateToken(user);

        var data = AuthDataResponse.builder()
                .token(token)
                .username(((UserDetails) user).getUsername())
                .role((user).getRole().name())
                .build();

        auditLogService.logNewUserLogin( user);

        return buildSuccessResponse(HttpStatus.OK, "User Authenticated", data);
    }
}
