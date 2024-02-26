package hospital.server.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String lastName;
    private Date birthDate;
    private String sex;
    // Other fields like address, medical record, etc.
    @ManyToMany(mappedBy = "patients")
    private List<Hospital> hospitals;
    // Constructors, getters, and setters
}
