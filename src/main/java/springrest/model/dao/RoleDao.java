package springrest.model.dao;

import java.util.List;

import springrest.model.Role;
import javax.persistence.Entity;;

public interface RoleDao {

	public Role getRole( Long id );

    public List<Role> getRoles();

    public Role saveRole( Role role );
    
    public boolean deleteRole(Role role);
    
    public boolean isRoleExists(Role role);
	
}
