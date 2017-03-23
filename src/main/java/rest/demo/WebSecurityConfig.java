package rest.demo;

import java.io.IOException;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import rest.demo.security.CsrfHeaderFilter;
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
	public CustomUserDetailsService customUserDetailsService() {
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
	
	@Bean
	public CsrfHeaderFilter csrfFilter() {
		return new CsrfHeaderFilter();
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.httpBasic()
		.and()
		.addFilterAfter(csrfFilter(), CsrfFilter.class)
		.addFilter(casAuthenticationFilter())
		.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
		.addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class)
		.addFilterAfter(switchUserFilter(), FilterSecurityInterceptor.class)
		.addFilterBefore(corsFilter(), ChannelProcessingFilter.class);
		
		
		http.exceptionHandling().defaultAuthenticationEntryPointFor(casAuthenticationEntryPoint(), new AntPathRequestMatcher("/admin/**"));
		
		http.csrf().csrfTokenRepository(csrfTokenRepository());
		http.headers().frameOptions().disable();
		
		http.formLogin()
			.successForwardUrl("/auth")
			.failureHandler((req, res, ex) -> { 
				res.sendError(HttpStatus.FORBIDDEN.value(), "Login fehlgeschlagen");
			});
		
		http.authorizeRequests().antMatchers("/api/**").hasRole("ADMIN");
		
		http.authorizeRequests()
			.antMatchers("/impersonate/logout/*").authenticated()
			.antMatchers("/impersonate/login/*").hasRole("ADMIN")
			.antMatchers("/admin/**").hasRole("ADMIN");
		
		http.logout()
			.logoutUrl("/logut")
			.logoutSuccessUrl("/auth")
			.invalidateHttpSession(true)
			.deleteCookies("JSESSIONID")
			.permitAll();
	}

	
	private CsrfTokenRepository csrfTokenRepository() {
		  HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		  repository.setHeaderName("X-XSRF-TOKEN");
		  return repository;
	}
	
	@Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(casAuthenticationProvider());
        auth.inMemoryAuthentication().withUser("admin").password("admin").roles("ADMIN").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
	}
	
	@Bean
	public SwitchUserFilter switchUserFilter() {
		
		SwitchUserFilter switchUserFilter = new SwitchUserFilter();
		switchUserFilter.setUserDetailsService(customUserDetailsService());
		switchUserFilter.setSwitchUserUrl("/impersonate/login");
	    switchUserFilter.setExitUserUrl("/impersonate/logout");
	    switchUserFilter.setTargetUrl("/");
	    return switchUserFilter;
	}
	
	@Bean
	public CorsFilter corsFilter() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("DELETE");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedMethod("PATCH");
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
	

}
