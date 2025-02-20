package org.example.bookingapplication.repository.telegramchat;

import java.util.List;
import java.util.Optional;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.example.bookingapplication.model.user.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramChatRepository extends JpaRepository<TelegramChat, Long> {
    Optional<TelegramChat> getTelegramChatByUserEmail(String email);

    List<TelegramChat> findAllByUser_Roles_Name(RoleType.RoleName roleName);
}
