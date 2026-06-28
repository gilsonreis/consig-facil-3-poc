package br.com.faciltecnologia.consigfacil3.config;

import br.com.faciltecnologia.consigfacil3.security.SecurityFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SecurityFilter securityFilter;

    @Bean
    @Order(1) // Executa primeiro
    public SecurityFilterChain apiFilterChain(HttpSecurity http) {
        return http
                // Intercepta APENAS rotas que começam com /api/v1
                .securityMatcher("/api/v1/**", "/v3/api-docs/**", "/swagger-ui/**")
                .csrf(AbstractHttpConfigurer::disable)
                // Totalmente Stateless
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll();
                    req.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();
                    req.anyRequest().authenticated();
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    // ==========================================
    // CADEIA 2: Segurança do Admin (Sessão + Thymeleaf)
    // ==========================================
    @Bean
    @Order(2) // Executa se não for rota da API
    public SecurityFilterChain adminFilterChain(HttpSecurity http) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                // Deixa o Spring gerenciar a sessão (Stateful) para o Thymeleaf
                .authorizeHttpRequests(req -> {
                    req.requestMatchers(
                            "/admin/auth/login",
                            "/admin/css/**",
                            "/admin/js/**",
                            "/admin/images/**",
                            "/error"
                    ).permitAll();
                    req.requestMatchers("/admin/**").authenticated();
                    req.anyRequest().permitAll(); // Fallback
                })
                // Configura o login visual do próprio Spring (opcional, mas recomendado)
                .formLogin(form -> form
                        .loginPage("/admin/auth/login")
                        .loginProcessingUrl("/admin/auth/login")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/auth/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/auth/logout")
                        .logoutSuccessUrl("/admin/auth/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
