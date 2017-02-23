package rest.demo.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryLinksResource;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import rest.demo.model.es.IndexedCollection;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.repository.es.IndexedMaterialRepository;

@RepositoryRestController
@RequestMapping("/api/index/materials")
public class IndexedMaterialController implements ResourceProcessor<RepositoryLinksResource> {

	@Autowired
	IndexedMaterialRepository repository;
	
	@RequestMapping("/{id}")
	public ResponseEntity<Resource<IndexedMaterial>> getCollection(@PathVariable("id") Long id) {
		
		IndexedMaterial m = repository.findOne(id);
		Resource<IndexedMaterial> r = new Resource<IndexedMaterial>(m);
		return new ResponseEntity<Resource<IndexedMaterial>>(r, HttpStatus.OK);
		
	}
	
	@RequestMapping("/")
	public ResponseEntity<Resources<IndexedMaterial>> getAllCollections() {
		
		Resources<IndexedMaterial> resources = new Resources<IndexedMaterial>(repository.findAll());
	    return new ResponseEntity<Resources<IndexedMaterial>>(resources, HttpStatus.OK);
	}
	
	@Override
	public RepositoryLinksResource process(RepositoryLinksResource resource) {
		resource.add(linkTo(IndexedCollection.class).withSelfRel());
		return resource;
	}
	
}
