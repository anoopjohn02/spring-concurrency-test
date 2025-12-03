package anoop.spring.concurrency.test.service;

import anoop.spring.concurrency.test.data.AddressEntity;
import anoop.spring.concurrency.test.data.AddressRepository;
import anoop.spring.concurrency.test.data.UserEntity;
import anoop.spring.concurrency.test.data.UserRepository;
import anoop.spring.concurrency.test.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;

    public List<User> getAllUsers() {
        List<UserEntity> entities = userRepository.findAll();

        final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try (executor) {
            List<Future<User>> futures = entities.stream()
                    .map(user -> executor.submit(() -> this.toDto(user)))
                    .toList();

            return futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .toList();
        }
    }

    private User toDto(UserEntity entity) {
        AddressEntity address = addressRepository.findByUserId(entity.getId());
        return new User(entity.getId(), entity.getName(), entity.getRole(), address.getCountry());
    }
}
