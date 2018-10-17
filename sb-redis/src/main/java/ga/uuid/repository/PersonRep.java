package ga.uuid.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import ga.uuid.entity.Person;

/**
 * 继承 CrudRepository 不需要有任何实现即可拥有普通的 Crud 操作 (spring 默认通过动态代理实现)
 */
public interface PersonRep extends CrudRepository<Person, String> {
	
	/**
	 * 支持方法名查询
	 * 注意: name 字段必须建立索引
	 */
	List<Person> findByName(String name);
}
