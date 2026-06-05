package com.PetConnect.security;

import com.PetConnect.repositories.ExpiredTokenRepository;
import com.PetConnect.repositories.UserRepository;
import com.PetConnect.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExpiredTokenRepository tokenExpiradoRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();
        // Rotas públicas conforme SecurityConfiguration
        if (
            uri.startsWith("/auth/") ||
            uri.startsWith("/h2-console/") ||
            //uri.startsWith("/pets/") ||
            uri.equals("/html/login.html") ||
            uri.equals("/html/register.html") ||
            uri.equals("/html/home.html") ||
            uri.startsWith("/html/") ||
            uri.equals("/petSitterHome.html") ||
            uri.equals("/petOwnerHome.html") ||
            uri.equals("/style.css") ||
            uri.equals("/auth.js") ||
            uri.equals("/petSitterProfileCreate.js") ||
            uri.equals("/petOwnerProfileCreate.js") ||
            uri.equals("/petOwner.js") ||
            uri.equals("/petSitter.js") ||
            uri.equals("/register.js") ||
            uri.equals("/favicon.ico") ||
            uri.startsWith("/img/") ||
            uri.startsWith("/users/") ||
            uri.startsWith("/js/") ||
            uri.startsWith("/images/") ||
            uri.endsWith(".js") ||
            uri.endsWith(".css") ||
            uri.endsWith(".html") ||
            uri.equals("/")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = recoverToken(request);

        if (token != null) {

            if (tokenExpiradoRepository.existsByToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Sessão encerrada. Faça login novamente.\"}");
                return;
            }

            String email = tokenService.validateToken(token);

            if (email == null || email.isBlank()) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token inválido ou expirado.\"}");
                return;
            }

            UserDetails user = (UserDetails) userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Usuário não encontrado.\"}");
                return;
            }

        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}