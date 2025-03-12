package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@SQLDelete(sql = "UPDATE address SET is_deleted = TRUE WHERE id = ?")
@SQLRestriction("is_deleted = FALSE")
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String country;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String city;
    private String district;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String houseNumber;
    private String apartmentNumber;
    private String floor;
    @Column(nullable = false)
    private String zipCode;
    @Column(nullable = false)
    private boolean isDeleted = false;
}
