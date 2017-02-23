package rest.demo.repository.jpa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import rest.demo.model.jpa.Section;

@JaversSpringDataAuditable
public interface SectionRepository extends JpaRepository<Section, Long> {
		
}