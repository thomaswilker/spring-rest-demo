package rest.demo.service;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.log4j.Logger;
import org.javers.core.Javers;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class EntityChangeProcessor implements ChangeProcessor<Map<String, Set<InstanceId>>> {

	HashMap<String, Set<InstanceId>> affectedProperties = new HashMap<>();
	Logger log = Logger.getLogger(this.getClass());
	
	private Javers javers;
	
	public EntityChangeProcessor(Javers javers) {
		this.javers = javers;
	}

	@Override
	public void onCommit(CommitMetadata commitMetadata) {
	}

	@Override
	public void onAffectedObject(GlobalId globalId) {
	}

	@Override
	public void beforeChangeList() {
	}

	@Override
	public void afterChangeList() {
	}

	@Override
	public void beforeChange(Change change) {
	}

	@Override
	public void afterChange(Change change) {
	}

	@Override
	public void onPropertyChange(PropertyChange propertyChange) {
	}

	@Override
	public void onValueChange(ValueChange valueChange) {
	}

	@Override
	public void onReferenceChange(ReferenceChange referenceChange) {
		
		Optional<Object> previousObject = referenceChange.getLeftObject();
		Set<?> entity = Stream.of(previousObject.filter(o -> o != null && o.getClass().isAssignableFrom(InstanceId.class)).map(o -> (InstanceId) o).orElse(null)).collect(toSet());
		addChange(referenceChange, entity);
	}

	@Override
	public void onNewObject(NewObject newObject) {
	}

	@Override
	public void onObjectRemoved(ObjectRemoved objectRemoved) {
	}

	@Override
	public void onContainerChange(ContainerChange containerChange) {
		Set<?> affectedValues = Stream.concat(containerChange.getRemovedValues().stream(), containerChange.getAddedValues().stream()).collect(toSet());
		addChange(containerChange, affectedValues);
	}

	@Override
	public void onSetChange(SetChange setChange) {
	}

	@Override
	public void onArrayChange(ArrayChange arrayChange) {
	}

	@Override
	public void onListChange(ListChange listChange) {
	}

	@Override
	public void onMapChange(MapChange mapChange) {
	}

	private void addChange(PropertyChange property, Set<?> entities) {
		
		entities.stream().forEach(e -> System.out.println(e.getClass()));
		
		Set<InstanceId> e = entities.stream().filter(o -> o.getClass().isAssignableFrom(InstanceId.class))
		 		   					  .map(o -> (InstanceId) o)
		 		   					  .collect(toSet());	
		
		affectedProperties.put(property.getPropertyName(), e);
	
	}
	
	@Override
	public Map<String, Set<InstanceId>> result() {
		return affectedProperties;
	}

}
