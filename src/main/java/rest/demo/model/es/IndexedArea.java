package rest.demo.model.es;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown=true)
@Indexes(value=Area.class, isDefaultIndexClass=true)
public class IndexedArea extends IndexedEntity {

	String name;
	IndexedSection section;
	
}
