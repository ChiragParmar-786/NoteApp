package com.secure.Notes.Security;

import com.secure.Notes.Model.AppRole;
import com.secure.Notes.Model.Role;
import com.secure.Notes.Model.User;
import com.secure.Notes.Repository.RoleRepository;
import com.secure.Notes.Repository.UserRepository;
import com.secure.Notes.Security.JWT.AuthEntryPointJwt;
import com.secure.Notes.Security.JWT.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity(prePostEnabled = true,securedEnabled = true,jsr250Enabled = true)
public class SecurityConfig {

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .ignoringRequestMatchers("/api/auth/public/**")); //remove csrf for public pages
        http.authorizeHttpRequests(request
                ->request
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/csrf-token").permitAll()
                .requestMatchers("/api/auth/public/**").permitAll()
                .anyRequest().authenticated());
        //http.addFilterBefore(new CustomLogginFilter(), UsernamePasswordAuthenticationFilter.class);
        //http.addFilterAfter(new RequestValidationFilter(), CustomLogginFilter.class);
        //http.csrf(AbstractHttpConfigurer::disable);
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler));
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        http.cors(
                cors -> cors.configurationSource(corsConfigurationSource)
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository,PasswordEncoder passwordEncoder){

        return args -> {
                    Role userrole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                    .orElseGet(()->roleRepository.save(new Role(AppRole.ROLE_USER)));

                    Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                    .orElseGet(()->roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

                    if(!userRepository.existsByUserName("user1")) {
                        User user1 = new User("user1","user1@example.com",passwordEncoder.encode("password1"));
                        user1.setAccountNonLocked(false);
                        user1.setAccountNonExpired(true);
                        user1.setCredentialsNonExpired(true);
                        user1.setEnabled(true);
                        user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                        user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                        user1.setTwoFactorEnabled(false);
                        user1.setSignUpMethod("email");
                        user1.setRole(userrole);
                        userRepository.save(user1);
                    }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnabled(true);
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

        };
        }
    }

