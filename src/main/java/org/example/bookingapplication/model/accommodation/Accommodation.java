package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.example.bookingapplication.model.booking.Booking;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"type", "address", "size", "amenities", "bookings"})
@ToString(exclude = {"type", "address", "size", "amenities", "bookings"})
@Entity
@SQLDelete(sql = "UPDATE accommodations SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "accommodations")
public class Accommodation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private AccommodationType type;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "size_type_id", nullable = false)
    private SizeType size;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "accommodations_amenity_types",
            joinColumns = @JoinColumn(name = "accommodation_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_type_id")
    )
    private Set<AmenityType> amenities;
    @Column(nullable = false)
    private BigDecimal dailyRate;
    @OneToMany(mappedBy = "accommodation")
    private Set<Booking> bookings;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
