package dss.example.pr1.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Usuarios en memoria: administrador (todo) y estudiante (solo compras)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {

        UserDetails admin = User.withUsername("admin")
                .password(encoder.encode("admin"))
                .roles("ADMIN")
                .build();

        UserDetails estudiante = User.withUsername("estudiante")
                .password(encoder.encode("1234"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, estudiante);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // La consola H2 necesita excluir CSRF para poder utilizar su formulario.
            // El resto de la aplicación mantiene la protección CSRF predeterminada.
            .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))

            // La consola H2 se muestra dentro de un iframe del mismo origen
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth

                // Si se añaden endpoints REST, quedan reservados al administrador.
                .requestMatchers("/api/**").hasRole("ADMIN")

                // Public static pages
                .requestMatchers("/", "/index", "/index.html",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**", "/uploads/**")
                    .permitAll()

                // Consola H2 (depuración): solo administrador
                .requestMatchers(PathRequest.toH2Console()).hasRole("ADMIN")

                // Admin-only pages (van ANTES que el listado público: la primera regla que coincide gana)
                .requestMatchers("/admin/**",
                                 "/products/add",
                                 "/products/edit/**",
                                 "/products/delete/**")
                    .hasRole("ADMIN")

                // Public product browsing (MVC)
                .requestMatchers(HttpMethod.GET, "/products", "/products/**").permitAll()

                // Carrito accesible sin login (guión de la práctica)
                .requestMatchers("/cart/**").permitAll()

                // Everything else requires login
                .anyRequest().authenticated()
            )

            // Browser login
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                // ADMIN va a /admin; un usuario normal, al catálogo. Si venía de una URL protegida, vuelve a ella
                .successHandler((request, response, authentication) -> {
                    boolean esAdmin = authentication.getAuthorities().stream()
                            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                    SavedRequestAwareAuthenticationSuccessHandler handler =
                            new SavedRequestAwareAuthenticationSuccessHandler();
                    handler.setDefaultTargetUrl(esAdmin ? "/admin" : "/products");
                    handler.onAuthenticationSuccess(request, response, authentication);
                })
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
