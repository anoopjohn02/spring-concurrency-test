package anoop.spring.concurrency.test.controller;

import anoop.spring.concurrency.test.model.Response;
import anoop.spring.concurrency.test.model.User;
import anoop.spring.concurrency.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Response<User> getAllUsers() {
        Instant start = Instant.now();
        List<User> users = userService.getAllUsers();
        Instant end = Instant.now();
        return new Response<>(users, Duration.between(start, end).toString());
    }
}
