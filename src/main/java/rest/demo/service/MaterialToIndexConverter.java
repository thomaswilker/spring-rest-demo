package rest.demo.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import rest.demo.model.es.IndexedCollection.IndexedCollectionReduced;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.model.es.IndexedMaterial.IndexedMaterialReduced;
import rest.demo.model.jpa.Material;

@Component
@Transactional
public class MaterialToIndexConverter implements Converter<Material, IndexedMaterial> {

	@Qualifier("defaultConversionService")
	@Autowired
	@Lazy
	ConversionService conversionService;
	
	@Override
	public IndexedMaterial convert(Material m) {
		
		IndexedMaterial im = new IndexedMaterial();
		im.setId(m.getId());
		im.setName(m.getName());
		im.setDescription(m.getDescription());
		
		im.setCollections(m.getCollections().stream().map(c -> {
			List<Material> materials = c.getMaterials(); 
			int index = materials.indexOf(m);
			IndexedMaterialReduced next = null;
			if(materials.size() > index + 1) {
				Material n = materials.get(index+1);
				next = new IndexedMaterialReduced(n.getId(), n.getName());
			}
			
			System.out.println(next.getName());
			return new IndexedCollectionReduced(c.getId(), c.getName(), next);
		}).collect(toList()));
		
		return im;
	}

	
	
}
