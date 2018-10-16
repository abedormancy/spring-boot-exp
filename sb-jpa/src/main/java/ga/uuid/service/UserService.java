package ga.uuid.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ga.uuid.entity.User;
import ga.uuid.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	UserRepository userRepository;
	
	public List<User> getUsers() {
		return userRepository.findAll();
	}
	
	public User save(String firstName, String lastName) {
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return userRepository.save(user);
	}
	
	public void saveAll(List<User> users) {
		userRepository.saveAll(users);
	}
	
	
	public Optional<User> findUserById(Integer id) {
		return userRepository.findById(id);
//		return userRepository.getOne(id); // 延迟加载
	}
	
	public void delete(Integer id) {
		userRepository.deleteById(id);
	}
	
	public UserRepository dao() {
		return userRepository;
	}
	
}
