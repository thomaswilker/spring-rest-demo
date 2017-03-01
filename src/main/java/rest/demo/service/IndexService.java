package rest.demo.service;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.apache.log4j.Logger;
import org.javers.common.collections.Sets;
import org.javers.core.Javers;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.Data;
import rest.demo.model.es.IndexedCollection;
import rest.demo.model.es.IndexedMaterial;
import rest.demo.model.jpa.AbstractEntity;
import rest.demo.model.jpa.Area;
import rest.demo.model.jpa.Collection;
import rest.demo.model.jpa.Material;
import rest.demo.model.jpa.Section;
import rest.demo.repository.jpa.CollectionRepository;
import rest.demo.service.IndexService.FieldSelections.FieldType;

@Service
@Transactional
@Async
public class IndexService {

	Logger logger = Logger.getLogger(this.getClass());
	
	Repositories repositories;
	
	@Qualifier("defaultConversionService")
	@Autowired
	ConversionService conversionService;
	
	@Autowired
	Javers javers;
	
	@Autowired
	ApplicationContext context;
	
	private SpelExpressionParser parser = new SpelExpressionParser();
	
	@Autowired
	CollectionRepository collectionRepository;
	
	@PostConstruct
	public void postConstruct() {
		this.repositories = new Repositories(context);
	}
	

	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	public void invokeIndex(AbstractEntity o) {
		
		try {
			
			JpaRepository<? extends AbstractEntity, Long> repository = (JpaRepository) repositories.getRepositoryFor(o.getClass());
			Method m = this.getClass().getDeclaredMethod("index", o.getClass());
			m.invoke(this, o);
		} catch(Exception e) {
			logger.info(e.getMessage());
		}
	}
	
	@Data
	public static class FieldSelections {
		private Set<String> values = new HashSet<>();
		
		public enum  FieldType {
			OMIT,PICK,ALL
		}
		
		private FieldType type;
		public boolean hasType(FieldType type) {
			return this.type.equals(type);
		}
		
		public static FieldSelections of(FieldType type, String... values) {
			
			FieldSelections s = new FieldSelections();
			s.setType(type);
			s.setValues(Sets.asSet(values));
			return s;
		}
	}
	
	
	
	private boolean shouldReindex(Optional<CdoSnapshot> snapshot, FieldSelections selections) {
		
		boolean reindex = false;
		if(snapshot.isPresent()) {
		
			Set<String> changesSet = Sets.asSet(snapshot.get().getChanged());
			Set<String> selectionsSet = selections.getValues();
			BiFunction<Set<String>, Set<String>, Set<String>> method;
			method = selections.hasType(FieldType.OMIT) ? Sets::difference : Sets::intersection;
			reindex = selections.hasType(FieldType.ALL) || method.apply(changesSet, selectionsSet).size() > 0;
		}
		
		return reindex;
	}
	
	
	private void reindex(AbstractEntity o, FieldSelections selections, Runnable action) {
		Optional<CdoSnapshot> snapshot = javers.getLatestSnapshot(o.getId(), o.getClass());
		
		if(shouldReindex(snapshot, selections)) {
			action.run();		
		}
	}
	
	private <T> Stream<T> flatten(Iterable<T> iterable) {
		
		Stream<T> stream = StreamSupport.stream(iterable.spliterator(), false);
		return stream.flatMap(e -> e instanceof Iterable ? flatten((Iterable<T>) e) : Stream.of(e));
	}
	
	private <T extends AbstractEntity, S> void indexPath(String path, T object, Class<S> asClass) {
		
		
		boolean hasRepository = repositories.hasRepositoryFor(asClass);
		boolean canConvert = conversionService.canConvert(object.getClass(), asClass);
		if(hasRepository && canConvert) {
			ElasticsearchCrudRepository<S, Long> repository = (ElasticsearchCrudRepository<S, Long>) repositories.getRepositoryFor(asClass);
			Set<T> elements = parser.parseExpression(path).getValue(object, Set.class);
			flatten(elements).map(e -> conversionService.convert(e, asClass)).forEach(i -> repository.save(i));
		}
		
	}
	
	@SuppressWarnings(value={"unused"})
	private void index(Section section) {
		
		FieldSelections selections = FieldSelections.of(FieldType.PICK, "name"); 
		
		JqlQuery query = QueryBuilder.byInstanceId(section.getId(), section.getClass()).limit(1).build();
		List<Change> changes = javers.findChanges(query);
		
		
		Runnable action = () -> {
			indexPath("areas.![collections]", section, IndexedCollection.class);
		};
		
		reindex(section, selections, action);
	}
	
	@SuppressWarnings("unused")
	private void index(Area area) {
		
		FieldSelections selections = FieldSelections.of(FieldType.ALL); 
		
		Runnable action = () -> {
			indexPath("areas.![collections]", area, IndexedCollection.class);
		};
		
		reindex(area, selections, action);
	}
	
	@SuppressWarnings(value={"unused","unchecked"})
	private void index(Collection collection) {
		
		FieldSelections selections = FieldSelections.of(FieldType.ALL); 
		String s = javers.getJsonConverter().toJson(collection);
		
		Runnable action = () -> {
			indexPath("materials", collection, IndexedMaterial.class);
		};
		
		IndexedCollection ic = conversionService.convert(collection, IndexedCollection.class);
		ElasticsearchCrudRepository<IndexedCollection, Long> repository = (ElasticsearchCrudRepository<IndexedCollection, Long>) repositories.getRepositoryFor(IndexedCollection.class);
		repository.save(ic);
		
		reindex(collection, selections, action);
	}
	
	@SuppressWarnings(value={"unused","unchecked"})
	private void index(Material material) {
		
		FieldSelections selections = FieldSelections.of(FieldType.ALL); 
		
		Runnable action = () -> {
			indexPath("collections", material, IndexedCollection.class);
		};
		
		IndexedMaterial im = conversionService.convert(material, IndexedMaterial.class);
		ElasticsearchCrudRepository<IndexedMaterial, Long> repository = (ElasticsearchCrudRepository<IndexedMaterial, Long>) repositories.getRepositoryFor(IndexedMaterial.class);
		repository.save(im);
		
		
		reindex(material, selections, action);
	}
	
}
