package com.secure.Notes.Security;

import com.secure.Notes.Model.AppRole;
import com.secure.Notes.Model.Role;
import com.secure.Notes.Model.User;
import com.secure.Notes.Repository.RoleRepository;
import com.secure.Notes.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(request
                ->request.anyRequest().authenticated());
        http.csrf(AbstractHttpConfigurer::disable);
        http.httpBasic(withDefaults());
        return http.build();
    }


    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository){

        return args -> {
                    Role userrole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                                    .orElseGet(()->roleRepository.save(new Role(AppRole.ROLE_USER)));

                    Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                    .orElseGet(()->roleRepository.save(new Role(AppRole.ROLE_ADMIN)));

                    if(!userRepository.existsByUserName("user1")) {
                        User user1 = new User("user1","user1@example.com","{noop}password1");
                        user1.setAccountNonLocked(false);
                        user1.setAccountNonExpired(true);
                        user1.setCredentialsNonExpired(true);
                        user1.setEnable(true);
                        user1.setCredentialExpiryDate(LocalDate.now().plusYears(1));
                        user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                        user1.setTwoFactorEnabled(false);
                        user1.setSignUpMethod("email");
                        user1.setRole(userrole);
                        userRepository.save(user1);
                    }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", "{noop}adminPass");
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnable(true);
                admin.setCredentialExpiryDate(LocalDate.now().plusYears(1));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }

        };
        }
    }

