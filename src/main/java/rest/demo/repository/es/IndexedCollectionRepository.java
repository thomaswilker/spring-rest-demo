package rest.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import rest.demo.model.es.IndexedCollection;
import rest.demo.model.jpa.Collection;

@RepositoryRestResource(exported=false)
public interface IndexedCollectionRepository extends ElasticsearchCrudRepository<IndexedCollection, Long> {
	
}
