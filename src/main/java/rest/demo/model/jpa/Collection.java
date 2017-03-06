package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OrderColumn;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.annotation.ReIndex;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Collection extends JpaEntity {

	
	public Collection(String name) {
		setName(name);
	}
	
	private String name;
	
	@ManyToMany
	@OrderColumn
	private List<Area> areas = new ArrayList<Area>();
	
	@ManyToMany
	@OrderColumn
	@ReIndex
	private List<Material> materials = new ArrayList<Material>();
	
}
