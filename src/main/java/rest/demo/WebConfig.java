package rest.demo;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import rest.demo.model.es.IndexedArea;
import rest.demo.model.es.IndexedCollection;
import rest.demo.model.es.IndexedCollection.IndexedCollectionReduced;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.model.es.IndexedMaterial.IndexedMaterialReduced;
import rest.demo.model.es.IndexedSection;
import rest.demo.service.AreaToIndexConverter;
import rest.demo.service.CollectionToIndexConverter;
import rest.demo.service.MaterialToIndexConverter;
import rest.demo.service.SectionToIndexConverter;

@Configuration
public class WebConfig extends RepositoryRestMvcConfiguration {

	static Logger log = Logger.getLogger(WebConfig.class);

	@Autowired
	CollectionToIndexConverter collectionToIndexConverter;
	
	@Autowired
	SectionToIndexConverter sectionToIndexConverter;
	
	@Autowired
	AreaToIndexConverter areaToIndexConverter;

	@Autowired
	MaterialToIndexConverter materialToIndexConverter;

	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/assets/**").addResourceLocations("classpath:/static/");
	}

	
	@Override
	protected void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
		
		config.setBasePath("api");
		
		List<Class<?>> classes = Arrays.asList(
				IndexedCollection.class,
				IndexedMaterial.class,
				IndexedSection.class,
				IndexedArea.class,
				IndexedCollectionReduced.class,
				IndexedMaterialReduced.class
		);
		config.exposeIdsFor(classes.toArray(new Class[]{}));
	}
	
	
	
	@Override
	public void configureConversionService(ConfigurableConversionService conversionService) {
		conversionService.addConverter(collectionToIndexConverter);
		conversionService.addConverter(sectionToIndexConverter);
		conversionService.addConverter(areaToIndexConverter);
		conversionService.addConverter(materialToIndexConverter);
	}
}
