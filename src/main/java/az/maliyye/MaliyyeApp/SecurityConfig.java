package az.maliyye.MaliyyeApp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // BU HİSSƏ ƏN VACİBDİR: Spring-ə deyirik ki, istifadəçiləri bazadan oxusun
    @Bean
    public UserDetailsService userDetailsService(IstifadeciRepository repo) {
        return username -> {
            Istifadeci istifadeci = repo.findByUsername(username);
            if (istifadeci == null) {
                throw new UsernameNotFoundException("İstifadəçi tapılmadı: " + username);
            }
            return User.builder()
                    .username(istifadeci.getUsername())
                    .password(istifadeci.getPassword())
                    .roles(istifadeci.getRol()) // "ADMIN" və ya "USER"
                    .build();
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()
                        .requestMatchers("/hesabat", "/istifadeciler/**").hasRole("ADMIN")
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