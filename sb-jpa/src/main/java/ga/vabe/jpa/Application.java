package ga.vabe.jpa;

import ga.vabe.jpa.domain.User;
import ga.vabe.jpa.repository.UserRepository;
import ga.vabe.jpa.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.stream.IntStream;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Application implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }

    @Override
    public void run(String... args) throws Exception {
        IntStream.rangeClosed(1, 3).forEach(i -> {
            User user = userService.generateData(i);
            log.info("user: {}", user);
        });
    }
}
