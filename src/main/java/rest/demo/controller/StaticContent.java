package rest.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/app")
public class StaticContent {

	@RequestMapping
	public ModelAndView index() {
		
		return new ModelAndView("index");
	}
}
