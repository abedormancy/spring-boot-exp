package ga.uuid.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import ga.uuid.entity.User;

@Repository
public class UserRepositoryCustomImpl  implements UserRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<User> myQuery() {
		Query query = em.createQuery("from User where mod(id, 2) = ?");
		query.setParameter(0, 1);
		return query.getResultList();
	}

}
