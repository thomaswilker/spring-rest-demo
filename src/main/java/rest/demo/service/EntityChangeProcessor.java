package rest.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.javers.common.collections.Arrays;
import org.javers.common.collections.Lists;
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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class EntityChangeProcessor implements ChangeProcessor<Map<Class<?>, List<Change>>> {

	HashMap<Class<?>, List<Change>> changes = new HashMap<>();
	Logger log = Logger.getLogger(this.getClass());
	
	
	
	@Override
	public void onCommit(CommitMetadata commitMetadata) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAffectedObject(GlobalId globalId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void beforeChangeList() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterChangeList() {
		log.info("list changed");
	}

	@Override
	public void beforeChange(Change change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void afterChange(Change change) {
		addChange(change);
	}

	@Override
	public void onPropertyChange(PropertyChange propertyChange) {
	
	}

	@Override
	public void onValueChange(ValueChange valueChange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReferenceChange(ReferenceChange referenceChange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNewObject(NewObject newObject) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onObjectRemoved(ObjectRemoved objectRemoved) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onContainerChange(ContainerChange containerChange) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSetChange(SetChange setChange) {
		// TODO Auto-generated method stub
		
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

	private void addChange(Change c) {
		
		if(changes.containsKey(c.getClass())) {
			changes.get(c.getClass()).add(c);
		} else {
			changes.put(c.getClass(), Lists.asList(c));
		}
		
	}
	
	@Override
	public Map<Class<?>, List<Change>> result() {
		// TODO Auto-generated method stub
		return changes;
	}

}
