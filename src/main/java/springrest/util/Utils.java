package springrest.util;

import java.util.Iterator;
import java.util.Set;

import springrest.model.Role;
import springrest.model.User;


public class Utils {
	
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
	
	
	
	
	public static String generateVerificationCode() {
		String code = "";
		for (int i = 0; i < 4; i++) {
			code += (int)(Math.random()*9);
		}
		return code;
	}
	
	public static String generatePassword() {
		String password = "";
		char[] alphabet = "abcdefghijk~@#lmnopqrvwxyzABCD$%.EFGHIstuJK234LMNOPQRSTUVWXYZ0156789".toCharArray();
		for (int i = 0; i < 10; i++) {
			password += alphabet[(int)(Math.random()*68)];
		}
		return password;
	}
	
	
}
