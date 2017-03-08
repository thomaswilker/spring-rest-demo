package rest.demo.model.es;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.annotation.Indexes;
import rest.demo.model.jpa.Area;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Indexes(value=Area.class, isDefaultIndexClass=true)
public class IndexedArea extends IndexedEntity {

	String name;
	IndexedSection section;
	
}
