package org.example.bookingapplication.model.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Entity
@Table(name = "role_types")
public class RoleType implements GrantedAuthority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }

    public enum RoleName {
        CUSTOMER,
        ADMIN;

        public static List<RoleName> getRolesUpTo(RoleName highestRole) {
            List<RoleName> rolesUpTo = new ArrayList<>();
            for (RoleName role : RoleName.values()) {
                rolesUpTo.add(role);
                if (role.equals(highestRole)) {
                    break;
                }
            }
            return rolesUpTo;
        }
    }
}
