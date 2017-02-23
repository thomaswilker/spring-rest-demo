package rest.demo.repository.jpa;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;

import rest.demo.model.jpa.Area;

@JaversSpringDataAuditable
public interface AreaRepository extends JpaRepository<Area, Long> {
}