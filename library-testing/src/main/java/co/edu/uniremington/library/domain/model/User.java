package co.edu.uniremington.library.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "library_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private Boolean hasFines;

    // No-args constructor (required by JPA)
    public User() {}

    // Full constructor (useful for tests)
    public User(Long id, String name, String email, Boolean hasFines) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.hasFines = hasFines;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Boolean getHasFines() { return hasFines; }
    public void setHasFines(Boolean hasFines) { this.hasFines = hasFines; }
}
