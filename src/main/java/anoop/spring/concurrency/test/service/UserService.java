package anoop.spring.concurrency.test.service;

import anoop.spring.concurrency.test.data.AddressEntity;
import anoop.spring.concurrency.test.data.AddressRepository;
import anoop.spring.concurrency.test.data.PhotoEntity;
import anoop.spring.concurrency.test.data.PhotoRepository;
import anoop.spring.concurrency.test.data.PostEntity;
import anoop.spring.concurrency.test.data.PostRepository;
import anoop.spring.concurrency.test.data.UserEntity;
import anoop.spring.concurrency.test.data.UserRepository;
import anoop.spring.concurrency.test.model.Address;
import anoop.spring.concurrency.test.model.Photo;
import anoop.spring.concurrency.test.model.Post;
import anoop.spring.concurrency.test.model.User;
import anoop.spring.concurrency.test.model.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.StructuredTaskScope;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PhotoRepository photoRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    public List<User> getAllUsersParallelly() {
        List<UserEntity> entities = userRepository.findAll();
        // Fetch more user details or convert to dtos parallelly using virtual threads
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

    public UserDetails getUserDetails(int id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        AddressEntity addressEntity = addressRepository.findByUserId(id);
        List<Photo> photos = photoRepository.findByUserId(id)
                .stream().map(this::toPhoto).toList();
        List<Post> posts = postRepository.findByUserId(id)
                .stream().map(this::toPost).toList();
        return new UserDetails(userEntity.getName(), userEntity.getRole(),
                toAddress(addressEntity), photos, posts);
    }

    public UserDetails getUserDetailsParallelly(int id) {
        // Fetch more user details parallelly using virtual threads
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try (executor) {
            Future<UserEntity> userEntityFuture = executor.submit(() -> userRepository.findById(id).orElseThrow());
            Future<AddressEntity> addressEntityFuture = executor.submit(() -> addressRepository.findByUserId(id));
            Future<List<Photo>> photosFuture = executor.submit(() -> photoRepository.findByUserId(id)
                    .stream().map(this::toPhoto).toList());
            Future<List<Post>> postsFuture = executor.submit(() -> postRepository.findByUserId(id)
                    .stream().map(this::toPost).toList());

            UserEntity userEntity = userEntityFuture.get();
            AddressEntity addressEntity = addressEntityFuture.get();
            List<Photo> photos = photosFuture.get();
            List<Post> posts = postsFuture.get();
            return new UserDetails(userEntity.getName(), userEntity.getRole(),
                    toAddress(addressEntity), photos, posts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserDetails getUserDetailsUsingStructuredConcurrency(int id) {
        // Fetch user details parallelly using Structured Concurrency. If one fail everything will be failed.
        // Note: Since it is a preview feature not recommended for production now.
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var userEntityFuture = scope.fork(() -> userRepository.findById(id).orElseThrow());
            var addressEntityFuture   = scope.fork(() -> addressRepository.findByUserId(id));
            var photosFuture     = scope.fork(() -> photoRepository.findByUserId(id)
                    .stream().map(this::toPhoto).toList());
            var postsFuture     = scope.fork(() -> postRepository.findByUserId(id)
                    .stream().map(this::toPost).toList());

            scope.join();          // wait for all
            scope.throwIfFailed(); // throw if any failed

            UserEntity userEntity = userEntityFuture.get();
            AddressEntity addressEntity = addressEntityFuture.get();
            List<Photo> photos = photosFuture.get();
            List<Post> posts = postsFuture.get();

            return new UserDetails(userEntity.getName(), userEntity.getRole(),
                    toAddress(addressEntity), photos, posts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User toDto(UserEntity entity) {
        // Assume we need to fetch the country of the user which is stored in address table.
        AddressEntity address = addressRepository.findByUserId(entity.getId());
        return new User(entity.getId(), entity.getName(), entity.getRole(), address.getCountry());
    }

    private Address toAddress(AddressEntity entity) {
        return new Address(entity.getAddress(), entity.getCountry(), entity.getPinCode());
    }

    private Photo toPhoto(PhotoEntity entity) {
        return new Photo(entity.getUrl());
    }

    private Post toPost(PostEntity entity) {
        return new Post(entity.getHeading(), entity.getDescription());
    }
}
