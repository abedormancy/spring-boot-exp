package ga.uuid.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import ga.uuid.entity.Person;

@Repository
public class PersonRepCustomImpl implements PersonRepCustom {
	
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	
	@Override
	public List<Person> myQuery() {
		/*stringRedisTemplate.execute(new RedisCallback<List<Person>>() {
			@Override
			public List<Person> doInRedis(RedisConnection connection) throws DataAccessException {
				return null;
			}
		});*/
		List<Person> datas = stringRedisTemplate.execute((RedisConnection connection) -> {
			List<Person> result = new ArrayList<>();
			Set<byte[]> dataKeys = connection.sMembers("Person".getBytes());
			dataKeys.forEach(dataKey -> {
				// 数据是以 hash 类型保存的，key 使用此格式 "Person:UUID"
				String dataKeyStr = "Person:" + new String(dataKey);
				Map<byte[], byte[]> data = connection.hGetAll(dataKeyStr.getBytes());
				data.forEach((key, value) -> {
					System.out.println("key: " + key + ", value: " + value);
				});
				// 读取数据，并转换为 Person
//				String name = new String(data.get("name".getBytes()));
//				String company = new String(data.get("company".getBytes()));
//				String id = new String(data.get("id".getBytes()));
//				Person person = new Person();
//				person.setId(id);;
//				person.setAge(Integer.parseInt(age));
//				person.setCompany(company);
//				person.setName(name);
//				result.add(person);	
			});
			return result;
		});
		return datas;
	}

}
