package rest.demo.service;

import static java.util.stream.Collectors.toList;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
@Transactional
public class InitializationService implements ApplicationRunner {
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	AreaRepository areaRepository;
	
	@Autowired
	MaterialRepository materialRepository;
	
	@Autowired
	CollectionRepository collectionRepository;
	
	@Autowired
	ElasticsearchTemplate template;
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		
		template.deleteIndex(IndexedCollection.class);
		template.createIndex(IndexedCollection.class);
		template.putMapping(IndexedCollection.class);
		template.refresh(IndexedCollection.class);
		
		System.out.println("---------- Start transaction ----------");
		
		Section iw = new Section("Ingeneurswissenschaften", "iw");
		iw = sectionRepository.save(iw);
		iw = sectionRepository.findOne(iw.getId());
		
		Section nw = new Section("Naturwissenschaften", "nw");
		nw = sectionRepository.save(nw);
		
		Section gw = new Section("Gesellschaftswissenschaften", "gw");
		gw = sectionRepository.save(gw);
		
		List<Area> nwAreas = saveAreasWithSection(nw, "Biologie", "Physik", "Mathematik", "Chemie");
		List<Area> iwAreas = saveAreasWithSection(iw, "Maschinenbau", "Architektur", "Informatik", "Elektrotechnik und Informationstechnik");
		
		Area maschinenbau = iwAreas.get(0);
		Area informatik = iwAreas.get(2);
		Area mathe = nwAreas.get(2);
		
		
		Material m1 = new Material("Vorlesung 1", "Beschreibung");
		Material m2 = new Material("Vorlesung 2", "Beschreibung");
		Material m3 = new Material("Vorlesung 3", "Beschreibung");
		Material v1 = materialRepository.save(m1);
		materialRepository.save(m3);
		materialRepository.save(m2);
		
		iw.setName("Ingeneur");
		iw.setSlug("iws");
		sectionRepository.save(iw);
		
		saveCollection("Biofluidmechanik", Arrays.asList(maschinenbau), Arrays.asList(v1));
		saveCollection("Grundlagen der informatik I", Arrays.asList(informatik), Arrays.asList(m1,m2,m3));
		saveCollection("Grundlagen der informatik II", Arrays.asList(informatik), Arrays.asList());
		saveCollection("Algorithmik", Arrays.asList(informatik), Arrays.asList());
		saveCollection("Analysis", Arrays.asList(mathe), Arrays.asList());
		saveCollection("Kurvenschätzung", Arrays.asList(mathe), Arrays.asList());
		saveCollection("Mathematik III für Bauwesen", Arrays.asList(mathe), Arrays.asList());
		saveCollection("Gewöhnliche Differentialgleichungen", Arrays.asList(mathe), Arrays.asList());
		saveCollection("Kavitation", Arrays.asList(maschinenbau), Arrays.asList());
		saveCollection("Technische Fluidsysteme", Arrays.asList(maschinenbau), Arrays.asList());
		
		System.out.println(v1.getCollections().size());
		
		System.out.println("---------- End transaction ----------");
		
		//c1.getAreas().forEach(x -> System.out.println(x.getName()));
		//c1.setMaterials(Lists.asList(m1,m2,m3));
		
	}
	
	
	private List<Area> saveAreasWithSection(Section section, String... areaNames) {
		
		List<Area> areasList = Arrays.asList(areaNames).stream().map(s -> {
			return new Area(s, section);
		}).collect(Collectors.toList());
		
		return areasList.stream().map(a -> areaRepository.save(a)).collect(toList());
	}
	
	private Collection saveCollection(String name, List<Area> areas, List<Material> materials) {
		
		Collection c = new Collection(name);
		c.setAreas(areas);
		c.setMaterials(materials);
		return collectionRepository.save(c);
	}
	
}
