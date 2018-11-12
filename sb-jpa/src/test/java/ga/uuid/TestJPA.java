package ga.uuid;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import ga.uuid.entity.User;
import ga.uuid.repository.UserRepositoryCustom;
import ga.uuid.service.UserService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJPA {
	
	@Autowired
	UserService us;
	
	@Autowired
	UserRepositoryCustom custom;
	
	@Test
	public void add() {
		us.save("第一个", "holder");
		List<User> users = IntStream.rangeClosed(2, 10)
							.mapToObj(i -> new User("abe_" + i, "稳住_" + i))
							.collect(Collectors.toList());
		us.saveAll(users);
	}
	
	@Test
	public void findById() {
		Optional<User> user = us.findUserById(3);
		assertEquals(user.orElseThrow(RuntimeException::new).getFirstName(), "abe_3");
	}
	
	@Test
	public void getAll() {
		us.getUsers().forEach(System.out::println);
	}
	
	@Test
	public void methodQuery() {
		Optional<User> user = us.dao().findByLastNameAndId("稳住_3", 3);
		assertEquals(user.orElseThrow(RuntimeException::new).getFirstName(), "abe_3");
		assertEquals(us.dao().findByIdLessThan(3).size(), 2);
	}
	
	@Test
	public void customQuery() {
		custom.myQuery().forEach(System.out::println);
	}
	
	@Test
	public void annotationQuery() {
		String lastName = us.dao().findLastNameNativeByfirstName("%_3%");
		assertEquals(lastName, "稳住_3");
		User user = us.dao().findByQueryId(3);
		assertEquals(user.getId().intValue(), 3);
	}
	
	@Test
	public void pageTest() {
		System.out.println("------------------");
		us.dao().findByName("%abe%", PageRequest.of(2, 5, Direction.DESC, "id")).forEach(System.out::println);
		System.out.println("------------------");
	}
}
