package rest.demo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AdminController {

	@Autowired
	@Qualifier("objectMapper")
	private ObjectMapper mapper;
	
	SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
	
	@Autowired
	LogoutFilter logoutFilter;
	
	
	@RequestMapping("/auth")
	public ResponseEntity<Authentication> showAuth() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
		HttpStatus status = isAdmin ? HttpStatus.OK : HttpStatus.FORBIDDEN;
		return new ResponseEntity<Authentication>(auth, status);
	}
	
	private String asJson(Object o) {
		String json = null;
		try { json = mapper.writeValueAsString(o); } 
		catch (Exception e) { e.printStackTrace(); }
		return json;
	}
	
	@RequestMapping("/admin")
	public ModelAndView admin() {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ModelAndView mv = new ModelAndView("index");
		mv.setViewName("index");
		mv.addObject("auth", asJson(auth));
		
		return mv;
	}
	
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		
		logoutHandler.logout(request, response, authentication);
		return "redirect:auth";
	}
}
