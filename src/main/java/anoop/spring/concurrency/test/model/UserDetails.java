package anoop.spring.concurrency.test.model;

import java.util.List;

public record UserDetails(
        String name,
        String role,
        Address address,
        List<Photo> photos,
        List<Post> posts
) {
}
