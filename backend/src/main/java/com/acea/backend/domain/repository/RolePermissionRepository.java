package com.acea.backend.domain.repository;

import com.acea.backend.domain.entity.RolePermission;
import com.acea.backend.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, UUID> {
    List<RolePermission> findByRole(Role role);
    Optional<RolePermission> findByRoleAndPermission(Role role, String permission);
    void deleteByRoleAndPermission(Role role, String permission);
}
