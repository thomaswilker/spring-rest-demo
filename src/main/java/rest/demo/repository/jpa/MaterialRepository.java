package rest.demo.repository.jpa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import rest.demo.model.jpa.Material;

@JaversSpringDataAuditable
public interface MaterialRepository extends JpaRepository<Material, Long> {
}