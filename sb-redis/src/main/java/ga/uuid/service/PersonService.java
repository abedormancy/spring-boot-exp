package ga.uuid.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;

import ga.uuid.entity.Person;
import ga.uuid.repository.PersonRep;

@Service
public class PersonService {
	
	private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

	@Autowired
	private PersonRep rep;
	
	public List<Person> getPersons() {
		Iterable<Person> datas = rep.findAll();
		return Lists.newArrayList(datas);
	}
	
	public void save(Person person) {
		rep.save(person);
	}
	
	public List<Person> findByName(String name) {
		return rep.findByName(name);
	}
	
	@Cacheable(value = "PersonService.now")
	public String now() {
		String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
		logger.info("--------------- [{}] invoke now() method. ---------------", dateTime);
		return dateTime;
	}
}
