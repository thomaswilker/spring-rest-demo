package rest.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import rest.demo.model.es.IndexedMaterial;

public interface IndexedMaterialRepository extends ElasticsearchCrudRepository<IndexedMaterial, Long> {
	
}
