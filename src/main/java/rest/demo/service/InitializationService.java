package rest.demo.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import rest.demo.model.es.IndexedCollection;
import rest.demo.model.jpa.Area;
import rest.demo.model.jpa.Collection;
import rest.demo.model.jpa.Material;
import rest.demo.model.jpa.Section;
import rest.demo.repository.jpa.AreaRepository;
import rest.demo.repository.jpa.CollectionRepository;
import rest.demo.repository.jpa.MaterialRepository;
import rest.demo.repository.jpa.SectionRepository;

@Service
public class InitializationService implements ApplicationRunner {
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	AreaRepository areaRepository;
	
	@Autowired
	MaterialRepository materialRepository;
	
	@Autowired
	CollectionRepository collectionRepository;
	
	private List<Area> saveAreasWithSection(Section section, String... areaNames) {
		
		List<Area> areasList = Arrays.asList(areaNames).stream().map(s -> {
			return new Area(s, section);
		}).collect(Collectors.toList());
		
		return areaRepository.save(areasList);
	}
	
	
	private Collection saveCollection(String name, Area... areas) {
		
		Collection c = new Collection(name);
		c.setAreas(Arrays.asList(areas));
		return collectionRepository.save(c);
	}
	
	@Autowired
	ElasticsearchTemplate template;
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		
		template.deleteIndex(IndexedCollection.class);
		template.createIndex(IndexedCollection.class);
		template.putMapping(IndexedCollection.class);
		template.refresh(IndexedCollection.class);
		
		Section iw = new Section("Ingeneurswissenschaften", "iw");
		iw = sectionRepository.save(iw);
		System.out.println(iw.getAreas().size());
		
		iw = entityManager.find(Section.class, iw.getId());
		System.out.println(iw.getAreas().size());
		
		Section nw = new Section("Naturwissenschaften", "nw");
		nw = sectionRepository.save(nw);
		
		Section gw = new Section("Gesellschaftswissenschaften", "gw");
		gw = sectionRepository.save(gw);
		
		List<Area> nwAreas = saveAreasWithSection(nw, "Biologie", "Physik", "Mathematik", "Chemie");
		List<Area> iwAreas = saveAreasWithSection(iw, "Maschinenbau", "Architektur", "Informatik", "Elektrotechnik und Informationstechnik");
		
		Area maschinenbau = iwAreas.get(0);
		Collection c1 = saveCollection("Biofluidmechanik", maschinenbau);
		saveCollection("Kavitation", maschinenbau);
		saveCollection("Technische Fluidsysteme", maschinenbau);
		
		Area informatik = iwAreas.get(2);
		Collection c2 = saveCollection("Grundlagen der informatik I", informatik);
		saveCollection("Grundlagen der informatik II", informatik);
		saveCollection("Algorithmik", informatik);
		
		Area mathe = nwAreas.get(2);
		saveCollection("Analysis", mathe);
		saveCollection("Kurvenschätzung", mathe);
		saveCollection("Mathematik III für Bauwesen", mathe);
		saveCollection("Gewöhnliche Differentialgleichungen", mathe);
		
		
		Material m1 = new Material("Vorlesung 1", "Beschreibung");
		Material m2 = new Material("Vorlesung 2", "Beschreibung");
		Material m3 = new Material("Vorlesung 3", "Beschreibung");
		materialRepository.save(m1);
		materialRepository.save(m3);
		materialRepository.save(m2);
		
		c1.getMaterials().add(m1);
		c1.getMaterials().add(m3);
		c1.getMaterials().add(m2);
		c2.getMaterials().add(m1);
		c2.getMaterials().add(m2);
		
		collectionRepository.save(Arrays.asList(c1,c2));
		
		iw.setName("Ingeneur");
		iw.setSlug("iws");
		sectionRepository.save(iw);
		
	}
	
}
