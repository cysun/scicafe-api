package springrest.model.dao.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import springrest.model.Role;
import springrest.model.dao.RoleDao;

@Repository
public class RoleDaoImpl implements RoleDao {
	
	@PersistenceContext
    private EntityManager entityManager;

    @Override
    public Role getRole( Long id )
    {
        return entityManager.find( Role.class, id );
    }

    @Override
    public List<Role> getRoles()
    {
        return entityManager.createQuery( "from Role order by id", Role.class )
            .getResultList();
    }

    @Override
    @Transactional
    public Role saveRole( Role role )
    {
        return entityManager.merge( role );
    }
    
    @Override
	@Transactional
	public boolean deleteRole(Role role)
	{
    	try {
    		entityManager.remove(role);
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
	}

	@Override
	public boolean isRoleExists(Role role) {
		return entityManager.contains(role);
	}


}
