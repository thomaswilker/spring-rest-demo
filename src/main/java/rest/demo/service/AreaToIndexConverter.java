package rest.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import rest.demo.model.es.IndexedArea;
import rest.demo.model.es.IndexedSection;
import rest.demo.model.jpa.Area;

@Component
public class AreaToIndexConverter implements Converter<Area, IndexedArea> {

	@Qualifier("defaultConversionService")
	@Autowired
	@Lazy
	ConversionService conversionService;
	
	@Override
	public IndexedArea convert(Area area) {
		IndexedArea ia = new IndexedArea();
		ia.setId(area.getId());
		ia.setName(area.getName());
		ia.setSection(conversionService.convert(area.getSection(), IndexedSection.class));
		return ia;
	}

	
	
}
