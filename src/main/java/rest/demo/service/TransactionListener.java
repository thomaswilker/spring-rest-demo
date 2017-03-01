package rest.demo.service;

import javax.annotation.PostConstruct;

import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import rest.demo.aop.RepositoryAdvices.TestEvent;

@Component
public class TransactionListener {

	@Autowired
	ApplicationContext context;
	
	Repositories repositories;
	
	@Autowired
	Javers javers;
	
	@PostConstruct
	public void test() {
		this.repositories = new Repositories(context);
	}
	
	@TransactionalEventListener(phase=TransactionPhase.AFTER_COMMIT)
	public void listener(TestEvent event) {
		
		System.out.println("------------- index event ---------------");
		JpaRepository<?, Long> repository = (JpaRepository<?, Long>) repositories.getRepositoryFor(event.getEntity().getClass());
		Object o = repository.getOne(event.getEntity().getId());
		System.out.println(javers.getJsonConverter().toJson(o));
		
	}
}
