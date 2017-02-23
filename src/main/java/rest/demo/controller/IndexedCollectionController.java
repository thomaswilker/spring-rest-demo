package rest.demo.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import rest.demo.model.es.IndexedCollection;
import rest.demo.repository.es.IndexedCollectionRepository;

@RepositoryRestController
@RequestMapping("/api/index/collections")
public class IndexedCollectionController implements ResourceProcessor<RepositoryLinksResource> {

	@Autowired
	IndexedCollectionRepository repository;
	
	@RequestMapping("/{id}")
	public ResponseEntity<Resource<IndexedCollection>> getCollection(@PathVariable("id") Long id) {
		
		IndexedCollection m = repository.findOne(id);
		Resource<IndexedCollection> r = new Resource<IndexedCollection>(m);
		return new ResponseEntity<Resource<IndexedCollection>>(r, HttpStatus.OK);
		
	}
	
	@RequestMapping("/")
	public ResponseEntity<Resources<IndexedCollection>> getAllCollections() {
		
		Resources<IndexedCollection> resources = new Resources<IndexedCollection>(repository.findAll());
	    return new ResponseEntity<Resources<IndexedCollection>>(resources, HttpStatus.OK);
	}
	
	@Override
	public RepositoryLinksResource process(RepositoryLinksResource resource) {
		resource.add(linkTo(IndexedCollection.class).withSelfRel());
		return resource;
	}
	
}
