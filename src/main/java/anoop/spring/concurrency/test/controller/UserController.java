package anoop.spring.concurrency.test.controller;

import anoop.spring.concurrency.test.model.Response;
import anoop.spring.concurrency.test.model.User;
import anoop.spring.concurrency.test.model.UserDetails;
import anoop.spring.concurrency.test.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Response<List<User>> getAllUsers(
            @RequestParam(required = false, defaultValue = "true") boolean parallel
    ) {
        Instant start = Instant.now();
        List<User> users;
        if (parallel) {
            users = userService.getAllUsersParallelly();
        } else {
            users = userService.getAllUsers();
        }
        return new Response<>(users, timeInString(start), parallel);
    }

    @GetMapping("/{id}")
    public Response<UserDetails> getUserDetails(
            @PathVariable(name = "id") int id,
            @RequestParam(required = false, defaultValue = "true") boolean parallel
    ) {
        Instant start = Instant.now();
        UserDetails userDetails;
        if (parallel) {
            userDetails = userService.getUserDetailsParallelly(id);
        } else {
            userDetails = userService.getUserDetails(id);
        }
        return new Response<>(userDetails, timeInString(start), parallel);
    }

    private String timeInString(Instant start) {
        Instant end = Instant.now();
        int timeInMillis = (int) Duration.between(start, end).getSeconds() * 1000;
        return timeInMillis + " ms";
    }
}
