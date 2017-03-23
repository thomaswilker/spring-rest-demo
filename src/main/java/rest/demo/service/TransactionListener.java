package rest.demo.service;

import javax.annotation.PostConstruct;

import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.databind.ObjectMapper;

import rest.demo.model.jpa.JpaEntity;

@Component
public class TransactionListener {

	@Autowired
	ApplicationContext context;
	
	Repositories repositories;
	
	@Autowired
	IndexService indexService;
	
	@Autowired
	Javers javers;
	
	@PostConstruct
	public void test() {
		this.repositories = new Repositories(context);
	}
	
	@TransactionalEventListener(fallbackExecution=true)
	public void listener(JpaEntity e) {
		indexService.invokeIndex(e.getClass(), e.getId());
	}
}
