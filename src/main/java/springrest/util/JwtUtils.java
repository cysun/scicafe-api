package springrest.util;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import springrest.model.Role;
import springrest.model.User;

@Component
public class JwtUtils {


	private static String SECRET;
	
	@Value("${scicafe.jwt.secret}")
	public void setSECRET(String secret) {
		SECRET = secret;
	}

	public DecodedJWT decode(String token) {
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
		    	.withExpiresAt(new Date(System.currentTimeMillis() + (3 * 60 * 60 * 1000)))//expire in 3 hours
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
