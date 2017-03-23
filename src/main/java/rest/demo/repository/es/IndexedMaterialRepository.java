package rest.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import rest.demo.model.es.IndexedMaterial;

@RepositoryRestResource(exported=false)
public interface IndexedMaterialRepository extends ElasticsearchCrudRepository<IndexedMaterial, Long> {
	
}
