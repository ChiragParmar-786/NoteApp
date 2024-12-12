package com.secure.Notes.Repository;

import com.secure.Notes.Model.AppRole;
import com.secure.Notes.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Integer> {
    Optional<Role> findByRoleName(AppRole appRole);
}
