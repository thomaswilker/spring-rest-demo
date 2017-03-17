package rest.demo;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import rest.demo.security.AuthoritiesConstants;
import rest.demo.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${cas.server}")
	private String casServer;
	
	@Value("#{'${cas.server}'.concat('login')}")
	private String casServerLogin;
	
	@Value("#{'${cas.server}'.concat('logout')}")
	private String casServerLogout;
	
	@Value("${cas.service.security}")
	private String casServiceSecurity;
	
	@Value("${cas.service.home}")
	private String casServiceHome;
	
	@Value("#{'${app.admins}'.split(',')}") 
	Set<String> adminList;
	
	@Bean
	public ServiceProperties serviceProperties() {
		ServiceProperties sp = new ServiceProperties();
		sp.setService(casServiceSecurity);
		sp.setSendRenew(false);
		return sp;
	}
	

	@Bean
	public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
		return new CustomUserDetailsService(adminList);
	}
	
	@Bean
	public SessionAuthenticationStrategy sessionStrategy() {
		SessionAuthenticationStrategy sessionStrategy = new SessionFixationProtectionStrategy();
		return sessionStrategy;
	}

	@Bean
	public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
		return new Cas20ServiceTicketValidator(casServer);
	}

	@Bean
	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
		casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
		return casAuthenticationFilter;
	}

	@Bean
	public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
		System.out.println(casServerLogin);
		casAuthenticationEntryPoint.setLoginUrl(casServerLogin);
		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
		return casAuthenticationEntryPoint;
	}

	@Bean
	public CasAuthenticationProvider casAuthenticationProvider() {
		CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
		casAuthenticationProvider.setAuthenticationUserDetailsService(customUserDetailsService());
		casAuthenticationProvider.setServiceProperties(serviceProperties());
		casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
		casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
		return casAuthenticationProvider;
	}
	
	@Bean
	public SingleSignOutFilter singleSignOutFilter() {
		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
		return singleSignOutFilter;
	}

	@Bean
	public LogoutFilter requestCasGlobalLogoutFilter() {
		
		String logoutUrl = String.format("%s?service=%s", casServerLogout, casServiceHome);
		LogoutFilter logoutFilter = new LogoutFilter(logoutUrl, new SecurityContextLogoutHandler());
		logoutFilter.setFilterProcessesUrl("/logout");
		logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"));
		return logoutFilter;
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
		http.httpBasic().authenticationEntryPoint(casAuthenticationEntryPoint())
		.and().addFilter(casAuthenticationFilter())
		.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
		.addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class);

		
		
		http.headers().frameOptions().disable().and().authorizeRequests().antMatchers("/").permitAll()
				.antMatchers("/login", "/logout", "/secure").authenticated()
				.antMatchers("/**").hasAnyAuthority(AuthoritiesConstants.ADMIN)
				.anyRequest().authenticated();
		
		http.logout().logoutUrl("/logout").logoutSuccessUrl("/").invalidateHttpSession(true)
				.deleteCookies("JSESSIONID");
	}

	

}
