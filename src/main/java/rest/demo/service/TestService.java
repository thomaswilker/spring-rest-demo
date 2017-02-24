package rest.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import rest.demo.model.jpa.Area;
import rest.demo.repository.jpa.SectionRepository;

@Component
public class TestService {

	@Autowired
	SectionRepository repository;
	
	@Transactional
	public int test(Long id) {
		return repository.findOne(id).getAreas().size();
	}
}