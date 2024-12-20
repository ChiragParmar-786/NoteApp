package com.secure.Notes.Service;

import com.secure.Notes.DTO.UserDTO;
import com.secure.Notes.Model.User;


import java.util.List;

public interface UserService {

    void updateUserRole(Long userId,String roleName);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);

    User findByUserName(String username);
}
