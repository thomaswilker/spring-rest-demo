package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Collection extends AbstractEntity {

	
	public Collection(String name) {
		setName(name);
	}
	
	private String name;
	
	@ManyToMany
	@OrderColumn
	private List<Area> areas = new ArrayList<Area>();
	
	@ManyToMany
	@OrderColumn
	private List<Material> materials = new ArrayList<Material>();
	
}
