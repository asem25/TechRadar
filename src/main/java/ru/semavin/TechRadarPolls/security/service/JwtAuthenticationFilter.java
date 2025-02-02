package ru.semavin.TechRadarPolls.security.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.semavin.TechRadarPolls.dtos.TokenRequest;
import ru.semavin.TechRadarPolls.listener.TechRadarKafkaListener;
import ru.semavin.TechRadarPolls.producer.TechRadarKafkaProducer;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService customUserDetailsService;
    private final TechRadarKafkaProducer techRadarKafkaProducer;
    private final TechRadarKafkaListener techRadarKafkaListener;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService customUserDetailsService, TechRadarKafkaProducer techRadarKafkaProducer, TechRadarKafkaListener techRadarKafkaListener) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.techRadarKafkaProducer = techRadarKafkaProducer;
        this.techRadarKafkaListener = techRadarKafkaListener;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Received Token: {}", token);

            try {
                if(validateTokenAsync(token)) {
                    if (jwtTokenProvider.validateToken(token)) {
                        log.info("Token is valid.");
                        String email = jwtTokenProvider.getEmailFromToken(token);
                        log.info("Extracted email from token: {}", email);

                        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else {
                        SecurityContextHolder.clearContext();
                        log.warn("Token validation failed.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Token expired or invalid");
                        return;
                    }
                }else{
                    SecurityContextHolder.clearContext();
                    log.warn("Token validation failed.");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Token invalid");
                    return;
                }
            } catch (Exception e) {

                response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
                response.getWriter().write("auth server is not working");
            }
        }else{
            log.warn("Auth headers is empty");
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
    private boolean validateTokenAsync(String token) throws Exception {
        CompletableFuture<Boolean> future = techRadarKafkaListener.validateTokenAsync("validate");

        techRadarKafkaProducer.sendValidateEvent("validate",
                TokenRequest.builder()
                .jwtToken(token)
                .build());

        return future.get(3, TimeUnit.SECONDS);
    }


}
