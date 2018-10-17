package ga.uuid;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import ga.uuid.entity.Account;
import ga.uuid.entity.Person;
import ga.uuid.repository.PersonRepCustom;
import ga.uuid.service.AccountService;
import ga.uuid.service.PersonService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestRedis {
	
	@Autowired
	StringRedisTemplate stringRedisTemplate; // 顾名思义, k-v 是字符串
	@Autowired
	RedisTemplate<String, Object> redisTemplate;

	@Autowired
	PersonService personService;
	@Autowired
	PersonRepCustom rep;
	
	@Autowired
	AccountService accountService;
	
	@Test
	public void testStringRedisTemplate() {
		String key = "that key";
		String value = "string值";
		stringRedisTemplate.opsForValue().set(key, value);
		String _value = stringRedisTemplate.opsForValue().get(key);
		assertEquals(_value, value);
		Integer x = stringRedisTemplate.opsForValue().append(key + "_append", value);
		assertEquals(x.intValue(), 0);
	}
	
	@Test
	public void testRedisTemplate() {
		Person p = new Person();
		p.setName("abeholder");
		p.setAge(18);
		p.setCompany("嘟嘟噜~");
		redisTemplate.opsForValue().set("abe", p);
		redisTemplate.opsForValue().set("abe1", p);
		Person _p = (Person) redisTemplate.opsForValue().get("abe");
		Person _p1 = (Person) redisTemplate.opsForValue().get("abe1");
		assertEquals(_p1.getCompany(), _p.getCompany());
	}
	
	@Test
	public void testCache01() throws InterruptedException {
		String last = personService.now();
		TimeUnit.SECONDS.sleep(2);
		System.out.println(last.equals(personService.now()));
	}
	
	@Test
	public void testWithSpringData() {
		int last_count = personService.getPersons().size();
		Person person = new Person();
		person.setAge(28);
		person.setName("嘟嘟噜");
		person.setCompany("anywhere.");
		personService.save(person);
		person = new Person();
		person.setAge(27);
		person.setName("abe");
		personService.save(person);
		int count = personService.getPersons().size();
		assertEquals(last_count, count - 2);
	}
	
	@Test
	public void testMethodQuery() {
		System.out.println("-----方法名查询-----");
		List<Person> list = personService.findByName("abe");
		boolean flag = list.stream().allMatch(p -> {
			return "abe".equals(p.getName());
		}) || list.size() == 0;
		assertTrue(flag);
	}
	
	@Test
	public void testCustom() {
		System.out.println(" ------ 自定义查询 ------");
//		personService.getPersons().forEach(System.out::println);
//		rep.myQuery().forEach(System.out::println);
	}
	
	
	@Test
	public void testRandomAccounts() throws InterruptedException {
		List<Account> accounts = accountService.generateAccounts(10);
		TimeUnit.SECONDS.sleep(2);
		List<Account> accounts1 = accountService.generateAccounts(10);
		assertEquals(accounts.get(0).getUsername(), accounts1.get(0).getUsername());
		TimeUnit.SECONDS.sleep(2);
		assertNotEquals(accounts.get(0).getUsername(), accounts1.get(0).getUsername());
	}
}
