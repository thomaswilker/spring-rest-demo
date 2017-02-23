package rest.demo.aop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import rest.demo.model.jpa.AbstractEntity;
import rest.demo.service.IndexService;

@Component
@Aspect
public class RepositoryAdvices {

	Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	@Autowired
	IndexService indexService;
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.save(..)) && args(o)")
    public void entitiesSave(AbstractEntity o) {
		logger.info(String.format("save entity of class %s with id %s", o.getClass(), o.getId()));
		indexService.invokeIndex(o);
	}
	
	@AfterReturning("execution(* rest.demo.repository.jpa.*.save(..)) && args(c)")
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
