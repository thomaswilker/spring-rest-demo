package rest.demo.repository.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

import rest.demo.model.es.IndexedCollection;
import rest.demo.model.jpa.Collection;

public interface IndexedCollectionRepository extends ElasticsearchCrudRepository<IndexedCollection, Long> {
	
}
