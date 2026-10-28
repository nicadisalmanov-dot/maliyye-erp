package az.maliyye.MaliyyeApp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // YENİ: @PreAuthorize üçün
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // YENİ: @PreAuthorize anotasiyasını aktivləşdirir
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(IstifadeciRepository repo) {
        return username -> {
            Istifadeci istifadeci = repo.findByUsername(username);
            if (istifadeci == null) {
                throw new UsernameNotFoundException("İstifadəçi tapılmadı: " + username);
            }

            // Bazada "ROLE_ADMIN" və ya "ROLE_USER" yazılıb. Onu birbaşa Authority kimi qəbul edirik.
            return new User(
                    istifadeci.getUsername(),
                    istifadeci.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority(istifadeci.getRol()))
            );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF qorumasını söndürürük (Formların işləməsi üçün)
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()

                        // İstifadəçi idarəetməsi yalnız ADMIN üçündür
                        .requestMatchers("/istifadeciler/**").hasAuthority("ROLE_ADMIN")

                        // Ana səhifəyə (Dashboard) giriş üçün ən azı bir rol lazımdır
                        .requestMatchers("/").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER")

                        // Digər bütün səhifələr üçün giriş tələb olunur
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}