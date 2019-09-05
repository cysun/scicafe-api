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
import springrest.model.Program;
import springrest.model.Reward;
import springrest.model.Role;
import springrest.model.dao.ProgramDao;
import springrest.model.dao.RoleDao;
import springrest.model.dao.UserDao;
import springrest.util.MailUtils;
import springrest.util.Utils;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.persistence.Entity;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class UserController {

    @Autowired
    private UserDao userDao;
    
    @Autowired
    private ProgramDao programDao;
    
    @Autowired
    private RoleDao roleDao;
    
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
    
    // is Username exists
    @RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
    public Boolean isUsernameExists( @PathVariable String username ,HttpServletRequest request)
    {
    	System.out.println("xxxxxx");
    	try {
        	User user = userDao.getUserByUsername(username);
        	if (user == null) 
        		return false;
        	else
        		return true;
    	} catch (Exception e) {
    		throw new RestException(400, e.getMessage());
    	}
    	
    }
    
    // is email exists
    @RequestMapping(value = "/email/{email}", method = RequestMethod.GET)
    public Boolean isEmailExists( @PathVariable String email ,HttpServletRequest request)
    {
    	email = email.replace("itsadot426", ".");
    	System.out.println(email);
    	try {
        	User user = userDao.getUserByEmail(email);
        	if (user == null) 
        		return false;
        	else
        		return true;
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
    	if (user.getFirstName() == null || user.getLastName() == null || user.getUsername() == null 
    			|| user.getPassword() == null || user.getEmail() == null)
    		throw new RestException( 400, "missing required field(s)." );
    	if (userDao.isUserExists(user)) {
            System.out.println("A User with name " + user.getFirstName() + " " + user.getLastName() + " already exist");
            return new ResponseEntity<User>(HttpStatus.CONFLICT);
        }
		try {
			Set<Role> roles = new HashSet<Role>();
//			if (user.getRoles()==null) 
			roles.add(new Role(new Long(0),"REGULAR"));
			System.out.println(roles);
			user.setRoles(roles);
	    	user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10)));
			return new ResponseEntity<User>(userDao.saveUser(user),HttpStatus.CREATED);
		} catch (Exception e) {
			throw new RestException(400, e.getMessage());
		}
	}
    
    @RequestMapping(value = "/verify/{email}", method = RequestMethod.GET)
    public JSONObject verifyEmail(@PathVariable String email,HttpServletRequest request) {
    	
    	JSONObject token = new JSONObject();
    	String code =  Utils.generateVerificationCode();
    	token.put("email",email);
    	token.put("code",code);
    	email = email.replace("itsadot426", ".");

    	try {
			MailUtils.sendMail(email, "Verication Code From Sci-Cafe", code);
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return token;
    }
    
    @RequestMapping(value = "/resetPassword/{email}", method = RequestMethod.GET)
    public Boolean resetPassword(@PathVariable String email,HttpServletRequest request) {
    	System.out.println("resetting password");
    	email = email.replace("itsadot426", ".");
    	String password = Utils.generatePassword();
    	try {
			User user = userDao.getUserByEmail(email);
			user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt(10)));
			userDao.saveUser(user);
			MailUtils.sendMail(email, "Your password have been reset", "Your username is " + user.getUsername() + " . And"
					+ "your password have been reset to " + password + ".");
			return true;
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
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
    		if (!Utils.proceedOnlyIfAdmin(requestUser) && !requestUser.getId().equals(id))
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id);
       		User user = userDao.getUser(id);
    		if (user == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
   			user.setFirstName(newUser.getFirstName());
   			user.setLastName(newUser.getLastName());
   			user.setEmail(newUser.getEmail());
   			System.out.println(newUser.getPosition());
   			user.setPosition(newUser.getPosition());
   			user.setUnit(newUser.getUnit());
   			if (!newUser.getPassword().equals("") && newUser.getPassword()!=null)
   				user.setPassword(BCrypt.hashpw(newUser.getPassword(), BCrypt.gensalt(10)));
   			user.setTitle(newUser.getTitle());
   			return new ResponseEntity<User>(userDao.saveUser(user), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
   	//delete user program
   	@RequestMapping(value = "/addUserProgram/{id}/{pid}", method = RequestMethod.PUT)
   	public ResponseEntity<User> deleteUserProgram(@PathVariable Long id, @PathVariable Long pid,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) && !requestUser.getId().equals(id))
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id + pid);
       		User user = userDao.getUser(id);
    		if (user == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		Program program = programDao.getProgram(pid);
    		System.out.println(program.getFullName()+user.getFirstName());
    		if (program == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		user.getPrograms().add(program);
   			return new ResponseEntity<User>(userDao.saveUser(user), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
	//delete user program
   	@RequestMapping(value = "/deleteUserProgram/{id}/{pid}", method = RequestMethod.PUT)
   	public ResponseEntity<User> addUserProgram(@PathVariable Long id, @PathVariable Long pid,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) && !requestUser.getId().equals(id))
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id + pid);
       		User user = userDao.getUser(id);
    		if (user == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		Program program = programDao.getProgram(pid);
    		System.out.println(program.getFullName()+user.getFirstName());
    		if (program == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		user.getPrograms().remove(program);
   			return new ResponseEntity<User>(userDao.saveUser(user), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
  //delete user program
   	@RequestMapping(value = "/addUserRole/{id}/{rid}", method = RequestMethod.PUT)
   	public ResponseEntity<User> AddUserRole(@PathVariable Long id, @PathVariable Long rid,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) && !requestUser.getId().equals(id))
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id + rid);
       		User user = userDao.getUser(id);
    		if (user == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		Role role = roleDao.getRole(rid);
    		if (role == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		user.getRoles().add(role);
   			return new ResponseEntity<User>(userDao.saveUser(user), HttpStatus.OK);
   		} catch (Exception e) {
   			throw new RestException(400, e.getMessage());
   		}
   	}
   	
  //delete user program
   	@RequestMapping(value = "/deleteUserRole/{id}/{rid}", method = RequestMethod.PUT)
   	public ResponseEntity<User> deleteUserRole(@PathVariable Long id, @PathVariable Long rid,HttpServletRequest request) {
   		try {
   			String token = request.getHeader("Authorization");
    		Utils.decode(token).getClaim("userId").asLong();
    		User requestUser = userDao.getUser(Utils.decode(token).getClaim("userId").asLong());
    		if (!Utils.proceedOnlyIfAdmin(requestUser) && !requestUser.getId().equals(id))
    			throw new RestException(400, "Invalid Authorization");
    		System.out.println("Updating User " + id + rid);
       		User user = userDao.getUser(id);
    		if (user == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		Role role = roleDao.getRole(rid);
    		if (role == null)
    			return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
    		user.getRoles().remove(role);
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