package rest.demo.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Authenticate a user from the database.
 */
public class CustomUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

	
	
	private final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

	private Set<String> admins;

	public CustomUserDetailsService() {
		super();
	}

	/**
	 * @param admins
	 */
	public CustomUserDetailsService(Set<String> admins) {
		super();
		this.admins = admins;
	}

	
	private UserDetails getDetails(String username) {
	
		List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
		if (admins != null && admins.contains(username)) {
			grantedAuthorities.add(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN));
		} else {
			
			grantedAuthorities.add(new GrantedAuthority() {
				private static final long serialVersionUID = 1L;

				@Override
				public String getAuthority() {
					return AuthoritiesConstants.USER;
				}
			});
		}
		
		return new AppUserDetails(username, grantedAuthorities);
	}
	
	@Override
	public UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
		
		String username = token.getPrincipal().toString().toLowerCase();
		
		return getDetails(username);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		return getDetails(username);
	}
}