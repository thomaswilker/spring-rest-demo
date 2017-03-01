package rest.demo.aop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;
import rest.demo.model.jpa.AbstractEntity;
import rest.demo.repository.jpa.CollectionRepository;
import rest.demo.service.IndexService;

@Service
@Aspect
@Transactional
public class RepositoryAdvices {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	@Autowired
	CollectionRepository collectionRepository;
	
	@Autowired
	IndexService indexService;
	
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@Getter
	public class TestEvent extends ApplicationEvent {

		AbstractEntity entity;
		
		public TestEvent(Object source, AbstractEntity entity) {
			super(source);
			this.entity = entity;
		}
		
	}
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.save*(..)) && args(o)")
    public void entitiesSave(AbstractEntity o) throws InterruptedException {
		
		logger.info(String.format("save entity of class %s with id %s", o.getClass(), o.getId()));
		indexService.invokeIndex(o);
		
		applicationEventPublisher.publishEvent(new TestEvent(this, o));
		logger.info(String.format("end of class %s with id %s", o.getClass(), o.getId()));
	}
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.save*(..)) && args(c)")
	@Transactional
	public void entitiesSave(Iterable<? extends AbstractEntity> c) {
		
		c.forEach(o -> {
			logger.info(String.format("save entity of class %s with id %s", o.getClass(), o.getId()));
			indexService.invokeIndex(o);
		});				
	}
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.delete(..)) && args(o)") 
    public void entitiesDelete(AbstractEntity o) {
		System.out.println(String.format("delete entity of class %s with id %s", o.getClass(), o.getId()));
	}
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.delete(..)) && args(c)") 
    public void entitiesDelete(Iterable<? extends AbstractEntity> c) {
		c.forEach(o -> System.out.println(String.format("delete entity of class %s with id %s", o.getClass(), o.getId())));
	}
	
	
}
