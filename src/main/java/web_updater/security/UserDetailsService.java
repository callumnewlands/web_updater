package web_updater.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User u = userRepository.getOne(username);
		if (u == null) {
			throw new UsernameNotFoundException(username);
		}
		return org.springframework.security.core.userdetails.User
				.withUsername(u.getUsername())
				.password("{noop}" + u.getPassword()) // noop = no password encoder as it is unencrypted by JPA
				.roles(u.getRoles().toArray(String[]::new))
				.build();
	}
}
