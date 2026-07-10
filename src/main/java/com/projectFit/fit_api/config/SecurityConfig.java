package com.projectFit.fit_api.config;

import com.projectFit.fit_api.repository.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controladores
@RequiredArgsConstructor
public class SecurityConfig {

    private final SocioRepository socioRepository; // Inyectamos tu repositorio de socios

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();

        //ACÁ PASA LA MAGIA: El convertidor "inline" de roles
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            String auth0Id = jwt.getSubject(); // Sacamos el ID único de Google/Auth0

            return socioRepository.findByAuth0Id(auth0Id)
                    .map(socio -> {
                        String rol = socio.getRol().getNombreRol();
                        String rolFormateado = rol.startsWith("ROLE_") ? rol : "ROLE_" + rol;
                        // Devolvemos la autoridad dentro de un Singleton Set
                        return (java.util.Collection<org.springframework.security.core.GrantedAuthority>)
                                java.util.Collections.singleton((org.springframework.security.core.GrantedAuthority)
                                        new org.springframework.security.core.authority.SimpleGrantedAuthority(rolFormateado));
                    })
                    .orElse(java.util.Collections.emptySet()); // Si no existe, Set vacío
        });

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // La landing page informativa y pública que armamos al principio
                        .requestMatchers("/api/public/**").permitAll()
                        // Cualquier otra consulta al backend requiere estar logueado
                        .anyRequest().authenticated()
                )
                // Activamos el Resource Server de Auth0 usando nuestro convertidor de base de datos
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter))
                );

        return http.build();
    }
}