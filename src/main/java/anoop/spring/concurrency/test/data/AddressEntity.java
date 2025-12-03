package anoop.spring.concurrency.test.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "addresses")
@Data
public class AddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable=false)
    private int userId;

    @Column(name = "address", nullable=false)
    private String address;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "pin_code", nullable = false)
    private String pinCode;
}

