package rest.demo.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CsrfHeaderFilter extends OncePerRequestFilter {
	
	private String path = "/";
	
	public CsrfHeaderFilter(String path) {
		this.path = path;
	}
	
	@Autowired
	@Qualifier("objectMapper")
	private ObjectMapper mapper;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		Cookie csrfCookie = WebUtils.getCookie(request, "XSRF-TOKEN");
		
		if (csrf != null) {
			Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
			String token = csrf.getToken();
			System.out.println("csrf: " + token);
			
			if (cookie == null || token != null && !token.equals(cookie.getValue())) {
				cookie = new Cookie("XSRF-TOKEN", token);
				cookie.setPath(path);
				response.addCookie(cookie);
				response.addHeader("XSRF-TOKEN", token);
				System.out.println("XSRF-TOKEN : " + token);
			}
		}
		filterChain.doFilter(request, response);
	}
}