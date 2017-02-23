package rest.demo.service;

import static java.util.stream.Collectors.toList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import rest.demo.model.es.IndexedArea;
import rest.demo.model.es.IndexedCollection;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.model.jpa.Collection;

@Component
public class CollectionToIndexConverter implements Converter<Collection, IndexedCollection> {

	@Qualifier("defaultConversionService")
	@Autowired
	@Lazy
	ConversionService conversionService;
	
	@Override
	public IndexedCollection convert(Collection c) {
		
		IndexedCollection ic = new IndexedCollection();
		ic.setId(c.getId());
		ic.setName(c.getName());
		ic.setAreas(c.getAreas().stream().map(a -> conversionService.convert(a, IndexedArea.class)).collect(toList()));
		ic.setMaterials(c.getMaterials().stream().map(m -> conversionService.convert(m, IndexedMaterial.class)).collect(toList()));
		
		return ic;
	}

}
