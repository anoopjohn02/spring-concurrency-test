package anoop.spring.concurrency.test.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoEntity, Integer> {
    List<PhotoEntity> findByUserId(int userId);
}
