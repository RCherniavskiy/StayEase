package org.example.bookingapplication.repository.user;

import java.util.Optional;
import org.example.bookingapplication.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findUserByEmailWithRoles(String email);
}
