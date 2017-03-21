package rest.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class AdminController {

	@Autowired
	@Qualifier("objectMapper")
	private ObjectMapper mapper;
	
	@RequestMapping("/auth")
	public @ResponseBody String showAuth() throws JsonProcessingException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return mapper.writeValueAsString(auth);
	}
	
	private String asJson(Object o) {
		String json = "none";
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
	
}
