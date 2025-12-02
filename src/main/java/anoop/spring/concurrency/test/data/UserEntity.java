package anoop.spring.concurrency.test.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public record UserEntity(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        int id,

        @Column(nullable=false, unique =true, length =100)
        String name,

        @Column(nullable = false, unique = true, length = 100)
        String role
){}

