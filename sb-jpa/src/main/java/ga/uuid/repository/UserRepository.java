package ga.uuid.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ga.uuid.entity.User;

//

/**
 * 通过使用注解 @RepositoryDefinition(domainClass = User.class, idClass = Integer.class)
 * 或者下面的继承方式来进行 CRUD 操作
 * Spring 会为该接口生成代理类，默认使用 JDK 的动态代理
 */
public interface UserRepository extends JpaRepository<User, Integer> {
	
	// 方法名查询，根据规则  Spring Data 会自动生成相关代码
	
	Optional<User> findByLastNameAndId(String name, Integer id);
	
	List<User> findByIdLessThan(Integer id);
	
	@Query("from User where id = ?1")
	User findByQueryId(Integer id);
	
	@Query(value = "select last_name from customers_5 where first_name like ?1", nativeQuery = true)
	String findLastNameNativeByfirstName(String firstName);
	
	@Query("from User where firstName like ?1 or lastName like ?1")
	List<User> findByName(String name, Pageable pageable);
}
