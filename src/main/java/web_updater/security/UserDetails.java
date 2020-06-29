package web_updater.security;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

/**
 * Wraps org.springframework.security.core.userdetails.User to add custom user fields to @AuthorisationPrinciple User object
 */
public class UserDetails extends org.springframework.security.core.userdetails.User {

	private User user;

	public UserDetails(User user) {
		super(user.getEmail(),
				"{noop}" + user.getPassword(),
				rolesToAuthorities(user.getRoles()));
		this.user = user;
	}

	private static List<? extends GrantedAuthority> rolesToAuthorities(List<String> roles) {
		return roles.stream()
				.map(role -> {
					Assert.isTrue(!role.startsWith("ROLE_"), () -> role + " cannot start with ROLE_ (it is automatically added)");
					return new SimpleGrantedAuthority("ROLE_" + role);
				}).collect(Collectors.toList());
	}

	public String getName() {
		return user.getName();
	}

}
