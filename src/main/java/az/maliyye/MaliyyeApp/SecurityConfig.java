package az.maliyye.MaliyyeApp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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

            Set<GrantedAuthority> authorities = new HashSet<>();

            // İstifadəçinin əsas rolunu əlavə edirik
            if (istifadeci.getRol() != null && !istifadeci.getRol().isEmpty()) {
                authorities.add(new SimpleGrantedAuthority(istifadeci.getRol()));
            }

            // BÜTÜN MODUL İCAZƏLƏRİNİ DİNAMİK OLARAK SPRING SECURITY-YƏ ÖTÜRÜRÜK:
            if (istifadeci.isIcazeDashboard()) authorities.add(new SimpleGrantedAuthority("MODULE_DASHBOARD"));
            if (istifadeci.isIcazeKassa())     authorities.add(new SimpleGrantedAuthority("MODULE_KASSA"));
            if (istifadeci.isIcazeBank())      authorities.add(new SimpleGrantedAuthority("MODULE_BANK"));
            if (istifadeci.isIcazeKontragent())authorities.add(new SimpleGrantedAuthority("MODULE_KONTRAGENT"));
            if (istifadeci.isIcazeAnbar())     authorities.add(new SimpleGrantedAuthority("MODULE_ANBAR"));
            if (istifadeci.isIcazeSatis())     authorities.add(new SimpleGrantedAuthority("MODULE_SATIS"));
            if (istifadeci.isIcazeSatinalma()) authorities.add(new SimpleGrantedAuthority("MODULE_SATINALMA"));
            if (istifadeci.isIcazeHesabat())   authorities.add(new SimpleGrantedAuthority("MODULE_HESABAT"));
            if (istifadeci.isIcazeHr())        authorities.add(new SimpleGrantedAuthority("MODULE_HR"));

            return new User(
                    istifadeci.getUsername(),
                    istifadeci.getPassword(),
                    authorities
            );
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/access-denied").permitAll()

                        .requestMatchers("/istifadeciler/**").hasAuthority("ROLE_ADMIN")

                        .requestMatchers("/dashboard", "/dashboard/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_DASHBOARD")
                        .requestMatchers("/kassa", "/kassa/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_KASSA")
                        .requestMatchers("/bank", "/bank/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_BANK")
                        .requestMatchers("/kontragent", "/kontragent/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_KONTRAGENT")
                        .requestMatchers("/anbar", "/anbar/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_ANBAR")
                        .requestMatchers("/satis", "/satis/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_SATIS")
                        .requestMatchers("/satinalma", "/satinalma/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_SATINALMA")
                        .requestMatchers("/hesabat", "/hesabat/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_HESABAT")
                        .requestMatchers("/hr", "/hr/**").hasAnyAuthority("ROLE_ADMIN", "MODULE_HR")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", false)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")
                );

        return http.build();
    }
}