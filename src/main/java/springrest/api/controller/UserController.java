package springrest.api.controller;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.simple.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import springrest.api.error.RestException;
import springrest.model.User;
import springrest.model.Role;
import springrest.model.dao.UserDao;

import springrest.util.Utils;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

    @Autowired
    private UserDao userDao;
    
    // Get an user by id
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUser( @PathVariable Long id ,HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) || requestUser.getId() == id)
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Fetching User with id " + id);
        	User user = userDao.getUser(id);
        	if (user == null) {
                System.out.println("User with id " + id + " not found");
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<User>(user, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    	
    }

    // Get all users
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<List<User>> getUsers(HttpServletRequest request)
    {
    	try {
    		String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User user = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(user))
    			throw new RestException(400, "Invalid Authorization");
    		//System.out.println(!Utils.proceedOnlyIfAdmin(Utils.decode(token).getClaim("userId").asLong()));
    		List<User> users = userDao.getUsers();
       	 	if(users.isEmpty()){
                return new ResponseEntity<List<User>>(HttpStatus.NO_CONTENT);//You many decide to return HttpStatus.NOT_FOUND
            }
            return new ResponseEntity<List<User>>(users, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    	 
    }
    
    // Registration
    @RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<User> createUser(@RequestBody User user) {
    	System.out.println("Creating User " + user.getFirstName() + " " + user.getLastName());
    	if (user.getFirstName() == null || user.getLastName() == null || user.getPosition() == null || user.getUnit() == null 
    			|| user.getUsername() == null || user.getPassword() == null || user.getEmail() == null)
    		throw new RestException( 400, "missing required field(s)." );
    	if (userDao.isUserExists(user)) {
            System.out.println("A User with name " + user.getFirstName() + " " + user.getLastName() + " already exist");
            return new ResponseEntity<User>(HttpStatus.CONFLICT);
        }
		try {
			Set<Role> roles = new HashSet<Role>();
			roles.add(new Role(new Long(0),"REGULAR"));
			System.out.println(roles);
			user.setRoles(roles);
	    	user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
			return new ResponseEntity<User>(userDao.saveUser(user),HttpStatus.CREATED);
		} catch (Exception e) {
			throw new RestException(400, e.getMessage());
		}
	}
    
    //Login
    @RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONObject loginUser(@RequestBody JSONObject jsonObject, HttpServletRequest request) {
		try {
			User user = userDao.getUserByUsername((String) jsonObject.get("username"));
			boolean matched = BCrypt.checkpw((String) jsonObject.get("password"), user.getPassword());
			// Return token if they match
			if (matched) {
				JSONObject token = new JSONObject();
				String jwtToken = Utils.generateToken(user);
				token.put("jwt", jwtToken);
				return token;
			} else {
				throw new RestException(400, "username or password is wrong");
			}
		} catch (Exception e) {
			throw new RestException(400, e.getMessage());
		}
	}
    
    // edit a user
   	@RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
   	public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User newUser,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) || requestUser.getId() == id)
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id);
       		User user = userDao.getUser(id);
    		if (user == null)
    				return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
   			user.setFirstName(newUser.getFirstName());
   			user.setLastName(newUser.getLastName());
   			user.setEmail(newUser.getEmail());
   			user.setPosition(newUser.getPosition());
   			user.setUnit(newUser.getUnit());
   			user.setUsername(newUser.getUsername());
   			user.setPassword(newUser.getPassword());
   			user.setTitle(newUser.getTitle());
   			return new ResponseEntity<User>(userDao.saveUser(user), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
    // delete a user
 	@RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
 	public ResponseEntity<User> deleteUser(@PathVariable("id") long id,HttpServletRequest request) {
        System.out.println("Fetching & Deleting User with id " + id);
        try {
        	String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser))
    			throw new RestException(400, "Invalid Authorization");
    		User user = userDao.getUser(id);
            if (user == null) {
                System.out.println("Unable to delete. User with id " + id + " not found");
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
        	userDao.deleteUser(user);
        	return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
        	throw new RestException(400, e.getMessage());
        }   
    }
 	
 	@RequestMapping(value = "/profile", method = RequestMethod.GET)
 	public ResponseEntity<User> getUserProfile( HttpServletRequest request) {
 		try {
    		String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
        	if (requestUser == null) {
                return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
            }
            return new ResponseEntity<User>(requestUser, HttpStatus.OK);
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
 	}
 	
}