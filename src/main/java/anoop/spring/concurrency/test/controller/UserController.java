package anoop.spring.concurrency.test.controller;

import anoop.spring.concurrency.test.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/{version}/users", version = "1")
public class UserController {

    @GetMapping
    public List<User> getUsers() {
        return List.of();
    }
}
