package org.example.bookingapplication.repository.telgeramchat;

import java.util.Optional;
import org.example.bookingapplication.model.telegram.TelegramChat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TelegramChatRepository extends JpaRepository<TelegramChat, Long> {
    Optional<TelegramChat> getTelegramChatByUserEmail(String email);
}
