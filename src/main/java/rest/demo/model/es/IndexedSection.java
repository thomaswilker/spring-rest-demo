package rest.demo.model.es;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.annotation.Indexes;
import rest.demo.model.jpa.Section;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Indexes(value=Section.class, isDefaultIndexClass=true)
public class IndexedSection extends IndexedEntity {

	String name;
	String slug;
}
