package rest.demo.repository.jpa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import rest.demo.model.jpa.Collection;

@JaversSpringDataAuditable
public interface CollectionRepository extends JpaRepository<Collection, Long> {
	
	
}