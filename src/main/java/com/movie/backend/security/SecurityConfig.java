package com.movie.backend.security;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.movie.backend.security.jwt.AuthFilter;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private AuthenticationEntryPoint authenticationEntryPoint;

	@Autowired
	private AuthFilter authFilter;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)
	        throws Exception {

	    http
	        .csrf(csrf -> csrf.disable())

	        .cors(cors -> cors.configurationSource(configurationSource()))

	        .sessionManagement(session -> session
	            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        )

	        .authorizeHttpRequests(request -> request

	        	    // Public registration and login
	        	    .requestMatchers(HttpMethod.POST, "/users", "/auth/login")
	        	    .permitAll()

	        	    // Public movie and theatre details
	        	    .requestMatchers(HttpMethod.GET, "/movies/**", "/theatres/**")
	        	    .permitAll()

	        	    // Only ADMIN can add movies and theatres
	        	    .requestMatchers(HttpMethod.POST, "/movies/**", "/theatres/**")
	        	    .hasRole("ADMIN")

	        	    // Only ADMIN can update movies and theatres
	        	    .requestMatchers(HttpMethod.PUT, "/movies/**", "/theatres/**")
	        	    .hasRole("ADMIN")

	        	    // Only ADMIN can delete movies and theatres
	        	    .requestMatchers(HttpMethod.DELETE, "/movies/**", "/theatres/**")
	        	    .hasRole("ADMIN")
	        	    
	        	    .requestMatchers(HttpMethod.GET, "/showtimes/**")
	        	    .permitAll()

	        	.requestMatchers(
	        	    HttpMethod.POST,
	        	    "/showtimes/**"
	        	).hasRole("ADMIN")

	        	.requestMatchers(
	        	    HttpMethod.PUT,
	        	    "/showtimes/**"
	        	).hasRole("ADMIN")

	        	.requestMatchers(
	        	    HttpMethod.DELETE,
	        	    "/showtimes/**"
	        	).hasRole("ADMIN")
	        	
	        	  // All other APIs require authentication
        	    .anyRequest()
        	    .authenticated()
	        	)

	        .exceptionHandling(exception -> exception
	        	    .authenticationEntryPoint(authenticationEntryPoint)

	        	    .accessDeniedHandler((request, response, accessDeniedException) -> {
	        	        response.setStatus(HttpStatus.FORBIDDEN.value());
	        	        response.setContentType("application/json");
	        	        response.getWriter().write(
	        	            "{\"message\":\"Access Denied: ADMIN role required\"}"
	        	        );
	        	    })
	        	);

	    http.addFilterBefore(
	        authFilter,
	        UsernamePasswordAuthenticationFilter.class
	    );

	    return http.build();
	}

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) {

        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(
        	    List.of(
        	        "http://localhost:5173",
        	        "http://localhost:5174"
        	    )
        	);

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(List.of("*"));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}