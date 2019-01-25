package springrest.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import springrest.model.Role;
import springrest.model.User;
import springrest.model.dao.UserDao;

public class Utils {

	@Autowired
	private static UserDao userDao;
	
	public static String SECRET = "c3bff416-983f-4461-9275-132b22136944";
	
	public static boolean proceedOnlyIfAdmin(User user) {
		Set<Role> roles = user.getRoles();
		Iterator<Role> iterator = roles.iterator();
		boolean isValid = false;
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals("ADMIN"))
				isValid = true;
		}
		System.out.println(user.getUsername());
		return isValid;
	}
	
	public static boolean proceedOnlyIfAdminOrRegular(User user) {
		Set<Role> roles = user.getRoles();
		Iterator<Role> iterator = roles.iterator();
		boolean isValid = false;
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals("ADMIN") || role.getName().equals("REGULAR") )
				isValid = true;
		}
		return isValid;
	}
	
	public static boolean orgnaziedByEventOrganizor(User user) {
		Set<Role> roles = user.getRoles();
		Iterator<Role> iterator = roles.iterator();
		boolean isValid = false;
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals("EVENT_ORGANIZOR"))
				isValid = true;
		}
		return isValid;
	}
	
	public static boolean providedByRewardProvider(User user) {
		Set<Role> roles = user.getRoles();
		Iterator<Role> iterator = roles.iterator();
		boolean isValid = false;
		while (iterator.hasNext()) {
			Role role = iterator.next();
			if (role.getName().equals("REWARD_PROVIDER"))
				isValid = true;
		}
		return isValid;
	}
	
	
	public static DecodedJWT decode(String token) {
		try {
		    Algorithm algorithm = Algorithm.HMAC256(SECRET);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("auth0")
		        .acceptLeeway(1) 
		        .acceptExpiresAt(5) 
		        .build(); //Reusable verifier instance
		    DecodedJWT jwt = verifier.verify(token);
		    return jwt;
		} catch (JWTVerificationException exception){
		    //Invalid signature/claims
			return null;
		}
	}
	
	public static String generateToken(User user) {
		try {
			Set<Role> roles = user.getRoles();
			Iterator<Role> iterator = roles.iterator();
			boolean isAdmin = false;
			while (iterator.hasNext()) {
				Role role = iterator.next();
				if (role.getName().equals("ADMIN"))
					isAdmin = true;
			}
		    Algorithm algorithm = Algorithm.HMAC256(SECRET);
		    String jwtToken = JWT.create()
		    	.withIssuedAt(new Date(System.currentTimeMillis()))
		    	.withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))//expire in 1 hour
		        .withIssuer("auth0")
		        .withClaim("userId", user.getId())
		        .withClaim("firstName", user.getFirstName())
		        .withClaim("lastName", user.getLastName())
		        .withClaim("isAdmin", isAdmin)
		        .sign(algorithm);
		    return jwtToken;
		} catch (JWTCreationException exception){
		    //Invalid Signing configuration / Couldn't convert Claims.
			return "";
		}
	}
}
