package com.secure.Notes.Controller;

import com.secure.Notes.DTO.UserDTO;
import com.secure.Notes.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/")
//@PreAuthorize("hasRole('ROLE_ADMIN')") method level security
public class AdminController {

    UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers()
    {
          return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("/update-role")
    public ResponseEntity<String> updateUserRole(@RequestParam Long userId,@RequestParam String username){

         userService.updateUserRole(userId,username);
         return new ResponseEntity<>("User Role updated..",HttpStatus.OK);
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(userService.getUserById(id),
                HttpStatus.OK);
    }


}
