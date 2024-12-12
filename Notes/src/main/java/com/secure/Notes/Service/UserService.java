package com.secure.Notes.Service;

import com.secure.Notes.DTO.UserDTO;


import java.util.List;

public interface UserService {

    void updateUserRole(Long userId,String roleName);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(Long id);
}
