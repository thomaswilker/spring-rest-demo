package rest.demo.aop;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import rest.demo.model.es.IndexedCollection;
import rest.demo.model.es.IndexedEntity;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.model.jpa.JpaEntity;
import rest.demo.repository.jpa.CollectionRepository;
import rest.demo.service.IndexService;

@Service
@Aspect
@Transactional
public class RepositoryAdvices {
	
	Logger log = Logger.getLogger(this.getClass());

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	@Autowired
	CollectionRepository collectionRepository;
	
	@Autowired
	IndexService indexService;
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;
	
	@AfterReturning(pointcut="execution(* rest.demo.repository.jpa.*.save*(..))", returning="o")
    public void entitiesSave(JpaEntity o) throws InterruptedException {
		
		applicationEventPublisher.publishEvent(o);
	}
	
	@AfterReturning(pointcut="execution(* rest.demo.repository.jpa.*.save*(..))", returning="c")
	public void entitiesSave(Iterable<? extends JpaEntity> c) {
		
		c.forEach(o -> {
			applicationEventPublisher.publishEvent(o);
		});				
	}
	
	@AfterReturning(pointcut="execution(* rest.demo.repository.jpa.*.delete(..))", returning="o") 
    public void entitiesDelete(JpaEntity o) {
		log.info(String.format("delete entity of class %s with id %s", o.getClass(), o.getId()));
	}
	
	@AfterReturning(pointcut="execution(* rest.demo.repository.jpa.*.delete(..))", returning="c") 
    public void entitiesDelete(Iterable<? extends JpaEntity> c) {
		c.forEach(o -> log.info(String.format("delete entity of class %s with id %s", o.getClass(), o.getId())));
	}
	
	@AfterReturning(pointcut="execution(* rest.demo.repository.es.*.save*(..))", returning="o")
    public void afterIndex(IndexedEntity o) {
		
		if(o.getClass().isAnnotationPresent(Document.class)) {
			Document d = o.getClass().getAnnotation(Document.class);
			String indexName = String.format("/%s", d.indexName());
			messagingTemplate.convertAndSend(indexName, o);
		} 
	}
	
//	@AfterReturning(pointcut="execution(* rest.demo.service.IndexService.invokeIndex(..))", returning="o")
//    public void afterIndex(IndexedCollection o) {
//		messagingTemplate.convertAndSend("/collections", o);
//	}
	
}
