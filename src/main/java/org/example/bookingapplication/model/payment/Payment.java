package org.example.bookingapplication.model.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.bookingapplication.model.booking.Booking;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Data
@EqualsAndHashCode(exclude = {"status", "booking"})
@ToString(exclude = {"status", "booking"})
@Entity
@SQLDelete(sql = "UPDATE bookings SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    @Column(name = "session_url", nullable = false)
    private String sessionUrl;
    @Column(name = "session_id", nullable = false)
    private String sessionId;
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;
    @Column(name = "is_deleted",nullable = false)
    private boolean isDeleted = false;
}
