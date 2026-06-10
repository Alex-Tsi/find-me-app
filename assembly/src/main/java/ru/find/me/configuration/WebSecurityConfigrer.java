package ru.find.me.configuration;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.find.me.UserService;
import ru.find.me.api.security.JwtAuthFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfigrer {

    private final UserService userService;
    private final JwtAuthFilter jwtAuthFilter;

    public WebSecurityConfigrer(@Qualifier("userServiceImpl") UserService userService,
                                JwtAuthFilter jwtAuthFilter) {
        this.userService = userService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationManager(authenticationManager())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/img/**", "/messenger/**", "/error").permitAll()
                        // Просмотр публикаций — публичный; создание/изменение/комментарии — под аутентификацией
                        .requestMatchers(HttpMethod.GET, "/api/publications", "/api/publications/filter",
                                "/api/publications/*", "/api/publications/user/**").permitAll()
                        .anyRequest().authenticated()
                )
                // Неаутентифицированный запрос к защищённому ресурсу → 401 ProblemDetail (а не дефолтный 403)
                .exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authEx) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/problem+json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                            "{\"type\":\"about:blank\",\"title\":\"Unauthorized\",\"status\":401,"
                                    + "\"detail\":\"Требуется аутентификация\"}");
                }))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
