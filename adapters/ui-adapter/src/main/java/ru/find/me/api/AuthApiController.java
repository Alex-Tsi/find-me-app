package ru.find.me.api;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.find.me.InvalidRefreshTokenException;
import ru.find.me.RefreshTokenService;
import ru.find.me.UserService;
import ru.find.me.api.dto.AuthRequest;
import ru.find.me.api.dto.AuthResponse;
import ru.find.me.api.dto.RegisterRequest;
import ru.find.me.api.security.JwtTokenProvider;
import ru.find.me.model.Profile;
import ru.find.me.model.Role;
import ru.find.me.model.User;

import java.time.Duration;
import java.util.Set;

/**
 * Аутентификация: access-токен (короткий JWT) в теле ответа + refresh-токен
 * (длинный, ротируемый) в httpOnly cookie. Cookie ограничена путём {@code /api/auth},
 * поэтому уходит только на эндпоинты refresh/logout, а не с каждым запросом.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    private static final String REFRESH_COOKIE = "refreshToken";
    private static final String REFRESH_COOKIE_PATH = "/api/auth";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final boolean cookieSecure;
    private final long refreshExpirationMs;

    public AuthApiController(AuthenticationManager authenticationManager,
                            JwtTokenProvider tokenProvider,
                            @Qualifier("refreshTokenServiceImpl") RefreshTokenService refreshTokenService,
                            @Qualifier("userServiceImpl") UserService userService,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.cookie.secure:false}") boolean cookieSecure,
                            @Value("${jwt.refresh-expiration-ms:604800000}") long refreshExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.cookieSecure = cookieSecure;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        User user;
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password()));
            user = (User) authentication.getPrincipal();
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Неверный логин или пароль");
        }
        return issueTokens(user);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        if (userService.findByUsername(request.username()) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Такой пользователь уже существует");
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setActive(true);
        user.setRoles(Set.of(Role.USER));
        user.setProfile(new Profile());
        userService.save(user);
        return issueTokens(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        if (refreshToken == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Отсутствует refresh-токен");
        }
        try {
            RefreshTokenService.RotationResult result = refreshTokenService.rotate(refreshToken);
            User user = userService.findById(result.userId());
            String accessToken = tokenProvider.generateToken(user.getUsername(), user.getId());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, refreshCookie(result.newRawToken()).toString())
                    .body(new AuthResponse(accessToken, user.getId(), user.getUsername()));
        } catch (InvalidRefreshTokenException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_COOKIE, required = false) String refreshToken) {
        if (refreshToken != null) {
            refreshTokenService.revoke(refreshToken);
        }
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearedRefreshCookie().toString())
                .build();
    }

    private ResponseEntity<AuthResponse> issueTokens(User user) {
        String accessToken = tokenProvider.generateToken(user.getUsername(), user.getId());
        String refreshToken = refreshTokenService.issue(user.getId());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie(refreshToken).toString())
                .body(new AuthResponse(accessToken, user.getId(), user.getUsername()));
    }

    private ResponseCookie refreshCookie(String value) {
        return baseCookie(value, Duration.ofMillis(refreshExpirationMs));
    }

    private ResponseCookie clearedRefreshCookie() {
        return baseCookie("", Duration.ZERO);
    }

    private ResponseCookie baseCookie(String value, Duration maxAge) {
        return ResponseCookie.from(REFRESH_COOKIE, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Lax")
                .path(REFRESH_COOKIE_PATH)
                .maxAge(maxAge)
                .build();
    }
}
