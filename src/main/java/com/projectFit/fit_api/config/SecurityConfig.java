package com.projectFit.fit_api.config;

import com.projectFit.fit_api.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final SocioRepository socioRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter convertidorJwt = new JwtAuthenticationConverter();

        // Mapear el JWT en la BD
        convertidorJwt.setJwtGrantedAuthoritiesConverter(jwt -> {
            String auth0Id = jwt.getSubject();

            return socioRepository.findByAuth0Id(auth0Id)
                    .map(socio -> {
                        String rol = socio.getRol().getNombreRol();
                        String rolFormateado = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;

                        return Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority(rolFormateado));
                    })
                    .orElseGet(() -> Collections.<GrantedAuthority>singletonList(new SimpleGrantedAuthority("ROLE_SOCIO")));
        });

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/pagos/webhook/**").permitAll()
                        .anyRequest().authenticated() // Todo lo demás pide estar logueado
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(convertidorJwt))
                );

        return http.build();
    }
}