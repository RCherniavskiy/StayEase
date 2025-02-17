package org.example.bookingapplication.repository.roletype;

import java.util.List;
import org.example.bookingapplication.model.user.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleTypeRepository extends JpaRepository<RoleType, Long> {
    List<RoleType> findRoleTypesByNameIn(List<RoleType.RoleName> roleNames);
}
