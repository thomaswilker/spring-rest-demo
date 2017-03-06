package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.annotation.ReIndex;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material extends JpaEntity {
	
	
	private String name;
	private String description;
	
	public Material(String name, String description) {
		setName(name);
		setDescription(description);
	}
	
	@Getter(onMethod = @__( @JsonIgnore ))
	@ManyToMany(mappedBy="materials")
	@ReIndex
	private List<Collection> collections = new ArrayList<>();
	

}
