package ga.vabe.jpa.service;

import ga.vabe.jpa.domain.User;
import ga.vabe.jpa.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository repository;

    @Override
    // @Async
    public User generateData(int index) {
        return repository.saveAndFlush(new User(null, "嗯，abe" + index, index, LocalDateTime.now()));
    }
}
