package rest.demo.service;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import rest.demo.model.es.IndexedSection;
import rest.demo.model.jpa.Section;


@Component
public class SectionToIndexConverter implements Converter<Section, IndexedSection> {

	@Override
	public IndexedSection convert(Section s) {
		IndexedSection is = new IndexedSection();
		is.setId(s.getId());
		is.setName(s.getName());
		is.setSlug(s.getSlug());
		return is;
	}

	
	
}
