package org.example.bookingapplication.repository.roletype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.example.bookingapplication.model.user.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleTypeRepositoryTest {
    private static final RoleType.RoleName CUSTOMER_ROLE_NAME = RoleType.RoleName.CUSTOMER;
    private static final RoleType.RoleName ADMIN_ROLE_NAME = RoleType.RoleName.ADMIN;
    @Autowired
    private RoleTypeRepository roleTypeRepository;

    @Test
    @DisplayName("Find role types with exist data and one name")
    void findRoleTypesByNameIn_findWithOneNameInList_ReturnListWithOneRoleType() {
        List<RoleType.RoleName> roleNameList = List.of(CUSTOMER_ROLE_NAME);
        List<RoleType> roleTypesByNameIn = roleTypeRepository.findRoleTypesByNameIn(roleNameList);
        assertEquals(1, roleTypesByNameIn.size());
        RoleType roleType = roleTypesByNameIn.get(0);
        assertEquals(CUSTOMER_ROLE_NAME, roleType.getName());
    }

    @Test
    @DisplayName("Find role types with exist data and two name")
    void findRoleTypesByNameIn_findWithTwoNameInList_ReturnListWithTwoRoleType() {
        List<RoleType.RoleName> roleNameList = List.of(CUSTOMER_ROLE_NAME, ADMIN_ROLE_NAME);
        List<RoleType> roleTypesByNameIn = roleTypeRepository.findRoleTypesByNameIn(roleNameList);
        assertEquals(2, roleTypesByNameIn.size());
        List<RoleType.RoleName> roleNamesFromRepo = roleTypesByNameIn.stream()
                .map(RoleType::getName)
                .toList();
        assertTrue(roleNamesFromRepo.containsAll(roleNameList));
    }
}
