package springrest.model.dao;

import java.util.List;

import springrest.model.User;

public interface UserDao {

    public User getUser( Long id );

    public List<User> getUsers();
    
    public User getUserByUsername( String username );

    public User saveUser( User user );
    
    public boolean deleteUser(User user);

	boolean isUserExists(User user);

}