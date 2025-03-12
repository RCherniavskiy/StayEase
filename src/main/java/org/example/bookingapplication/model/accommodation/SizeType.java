package org.example.bookingapplication.model.accommodation;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "size_types")
public class SizeType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,
            unique = true,
            columnDefinition = "varchar")
    @Enumerated(EnumType.STRING)
    private SizeTypeName name;

    public enum SizeTypeName {
        ONE_PEOPLE,
        TWO_PEOPLE,
        THREE_PEOPLE,
        FOUR_PEOPLE
    }
}
