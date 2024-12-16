package com.secure.Notes.Controller;

import com.secure.Notes.Model.AppRole;
import com.secure.Notes.Model.Role;
import com.secure.Notes.Model.User;
import com.secure.Notes.Repository.RoleRepository;
import com.secure.Notes.Repository.UserRepository;
import com.secure.Notes.Security.JWT.JwtUtils;
import com.secure.Notes.Security.SignIn.LoginRequest;
import com.secure.Notes.Security.SignIn.LoginResponse;
import com.secure.Notes.Security.SignUP.MessageResponse;
import com.secure.Notes.Security.SignUP.SignupRequest;
import com.secure.Notes.Security.SignUP.UserInfoResponse;
import com.secure.Notes.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AuthController {

    AuthenticationManager authenticationManager;

    JwtUtils jwtUtils;

    UserRepository userRepository;

    RoleRepository roleRepository;

    PasswordEncoder encoder;

    UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.userService = userService;
    }

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest){
        Authentication authentication;
        try{
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String,Object> map = new HashMap<>();
            map.put("message","Bad credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
        }

        //Set the authentication

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        //Collect roles from userDetails

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        LoginResponse response = new LoginResponse(userDetails.getUsername(),roles,jwtToken);

        return ResponseEntity.ok(response);

    }


    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest){
        if(userRepository.existsByUserName(signupRequest.getUsername())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken"));
        }

        if(userRepository.existsByEmail(signupRequest.getEmail())){
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken"));
        }

        User user = new User(signupRequest.getUsername(),
                signupRequest.getEmail(),
               encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();

        Role role;

        if(strRoles == null || strRoles.isEmpty()){
            role = roleRepository.findByRoleName(AppRole.ROLE_USER).
                    orElseThrow(()->new RuntimeException("Error: Role is not found"));
        } else{
            String roleStr = strRoles.iterator().next();
            if(roleStr.equals("admin")){
               role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN).
                       orElseThrow(()->new RuntimeException("Error: Role is not found"));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER).
                        orElseThrow(()->new RuntimeException("Error: Role is not found"));
            }
        }

        user.setAccountNonLocked(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
        user.setAccountExpiryDate(LocalDate.now().plusYears(1));
        user.setTwoFactorEnabled(false);
        user.setSignUpMethod("email");
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails){
        User user = userService.findByUserName(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
                );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public ResponseEntity<String> getUserName(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok().body(userDetails.getUsername());
    }

}
