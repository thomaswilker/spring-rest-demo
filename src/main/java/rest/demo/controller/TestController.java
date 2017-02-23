package rest.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.web.bind.annotation.RequestMapping;

import rest.demo.model.jpa.Section;
import rest.demo.repository.jpa.SectionRepository;

@RepositoryRestController
@RequestMapping("test")
public class TestController {

	@Autowired
	SectionRepository sectionRepository;
	
	@RequestMapping("/")
	public void removeConnection() {
		
		Section s = sectionRepository.findOne(1l);
		
		System.out.println(s.getAreas().size());
		
		
	}
	
}
