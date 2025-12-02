package anoop.spring.concurrency.test.service;

import anoop.spring.concurrency.test.data.UserEntity;
import anoop.spring.concurrency.test.data.UserRepository;
import anoop.spring.concurrency.test.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    private User toDto(UserEntity entity) {
        return new User(entity.id(), entity.name(), entity.role());
    }
}
