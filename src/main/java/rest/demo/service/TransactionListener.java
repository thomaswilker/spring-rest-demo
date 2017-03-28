package rest.demo.service;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.javers.core.Javers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import rest.demo.model.jpa.Collection;
import rest.demo.model.jpa.JpaEntity;
import rest.demo.model.jpa.Material;

@Component
public class TransactionListener {

	@Autowired
	ApplicationContext context;
	
	@Autowired
	IndexService indexService;
	
	@Autowired
	Javers javers;
	
	Logger log = Logger.getLogger(this.getClass());
	
	@TransactionalEventListener(fallbackExecution=true)
	public void listener(JpaEntity e) {
		indexService.invokeIndex(e);
		log.info("index entity " + e.getId());
	}
	
}
