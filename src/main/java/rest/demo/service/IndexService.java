package rest.demo.service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.javers.repository.jql.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import rest.demo.annotation.Indexes;
import rest.demo.annotation.PropertySelect;
import rest.demo.annotation.ReIndex;
import rest.demo.model.es.IndexedEntity;
import rest.demo.model.jpa.JpaEntity;

@Service
@Transactional
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
	
	private Set<Class<?>> jpaEntities = new HashSet<>();
	private Set<Class<?>> indexedEntities = new HashSet<>();

	
	@PostConstruct
	public void postConstruct() {
		this.repositories = new Repositories(context);
		FastClasspathScanner scanner = new FastClasspathScanner("rest.demo");
		
		List<String> jpa = scanner.scan().getNamesOfSubclassesOf(JpaEntity.class);
		List<String> indexed = scanner.scan().getNamesOfSubclassesOf(IndexedEntity.class);
		jpaEntities.addAll(scanner.scan().classNamesToClassRefs(jpa));
		indexedEntities.addAll(scanner.scan().classNamesToClassRefs(indexed));
	}
	
	@PersistenceContext(type = PersistenceContextType.EXTENDED)
	private EntityManager entityManager;
	
	
	private <E extends JpaEntity> Class<?> getIndexClassForEntityClass(E entity) {
		for (Class<?> indexedClass : indexedEntities) {
			Indexes indexes = indexedClass.getDeclaredAnnotation(Indexes.class);
			if (indexes != null && indexes.isDefaultIndexClass() && indexes.value().isAssignableFrom(entity.getClass()))
				return indexedClass;
		}
		
		return null;
	}
	
	
	private Stream<String> includePaths(Field f) {
		ReIndex reIndex = f.getAnnotation(ReIndex.class);
		String fieldName = f.getName();
		List<String> paths = Arrays.asList(reIndex.includePaths());
		
		return paths.stream()
					.map(p -> p.replaceFirst("this", fieldName))
					.map(p -> asSpelExpr(Arrays.asList(p.split("\\."))));
		
	}
	
	private String asSpelExpr(List<String> p) {
		return p.size() > 1 ? String.format("%s.![%s]", p.get(0), asSpelExpr(p.subList(1, p.size()))) : p.get(0);
	}
	
	
	
	@Async
	@SuppressWarnings("unchecked")
	public <T extends JpaEntity> void invokeIndex(T o) {
		
		JpaRepository<? extends JpaEntity, Long> jpaRepository = (JpaRepository<JpaEntity, Long>) repositories.getRepositoryFor(o.getClass());
		final JpaEntity entity = (T) jpaRepository.findOne(o.getId());
		
		index(entity);
		Arrays.asList(entity.getClass()
		      .getDeclaredFields())
			  .stream()
			  .filter(f -> f.isAnnotationPresent(ReIndex.class) && shouldReindex(entity, f.getAnnotation(ReIndex.class).conditional()))
			  .flatMap(this::includePaths)
			  .forEach(p -> indexPath(p, entity));
		
	}
	
	private <T extends JpaEntity> boolean shouldReindex(T entity, PropertySelect selections) {
		
		boolean reindex = true;
		
		Optional<CdoSnapshot> snapshot = javers.getLatestSnapshot(entity.getId(), entity.getClass());
		List<Change> changes = javers.findChanges(QueryBuilder.byInstanceId(entity.getId(), entity.getClass())
									 .limit(1)
									 .build());
		
		if(changes.size() > 0 && snapshot.isPresent()) {
			
			EntityChangeProcessor proc = context.getBean(EntityChangeProcessor.class);
			
			Map<Class<?>, List<Change>> changesByClass = javers.processChangeList(changes, proc);
			System.out.println(javers.getJsonConverter().toJson(changesByClass));
			
			Set<String> changesSet = Sets.asSet(snapshot.get().getChanged());
			Set<String> selectionsSet = Sets.asSet(selections.properties());
			
			logger.info(changesSet);
			logger.info(selectionsSet);
			
			BiFunction<Set<String>, Set<String>, Set<String>> method;
			method = selections.type().equals(PropertySelect.SelectType.OMIT) ? Sets::difference : Sets::intersection;
			reindex = selections.type().equals(PropertySelect.SelectType.ALL) || method.apply(changesSet, selectionsSet).size() > 0;
			logger.info(reindex);
		}
		
		return reindex;
	}
	
	
	private <T> Stream<T> flatten(Iterable<T> iterable) {
		
		Stream<T> stream = StreamSupport.stream(iterable.spliterator(), false);
		return stream.flatMap(e -> e instanceof Iterable ? flatten((Iterable<T>) e) : Stream.of(e));
	}
	
	private void index(JpaEntity entity) {
		
		Class<?> targetClass = getIndexClassForEntityClass(entity);
		
		if(targetClass != null && repositories.hasRepositoryFor(targetClass)) {
			IndexedEntity esObject = (IndexedEntity) conversionService.convert(entity, targetClass);
			ElasticsearchCrudRepository<IndexedEntity, Long> repository = (ElasticsearchCrudRepository<IndexedEntity, Long>) repositories.getRepositoryFor(targetClass);
			repository.save(esObject);
		}
	}
	
	private <T extends JpaEntity> void indexPath(String path, T object) {
		System.out.println("path:" + path);
		Set<T> elements = parser.parseExpression(path).getValue(object, Set.class);
		flatten(elements).forEach(this::index);
	}
	
	
}
