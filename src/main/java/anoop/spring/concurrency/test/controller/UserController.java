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
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Response<List<User>> getAllUsers(
            @RequestParam(required = false, defaultValue = "true") boolean parallel
    ) {
        LocalDateTime start = LocalDateTime.now();
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
        LocalDateTime start = LocalDateTime.now();
        UserDetails userDetails;
        if (parallel) {
            userDetails = userService.getUserDetailsParallelly(id);
        } else {
            userDetails = userService.getUserDetails(id);
        }
        return new Response<>(userDetails, timeInString(start), parallel);
    }

    @GetMapping("/{id}/structured")
    public Response<UserDetails> getUserDetailsByStructuredConcurrency(
            @PathVariable(name = "id") int id
    ) {
        LocalDateTime start = LocalDateTime.now();
        UserDetails userDetails = userService.getUserDetailsUsingStructuredConcurrency(id);
        return new Response<>(userDetails, timeInString(start), true);
    }

    private String timeInString(LocalDateTime start) {
        LocalDateTime end = LocalDateTime.now();
        long timeInMillis = TimeUnit.NANOSECONDS.toMillis(Duration.between(start, end).toNanos());
        return timeInMillis + " ms";
    }
}
