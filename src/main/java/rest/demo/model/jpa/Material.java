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

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Material extends AbstractEntity {
	
	
	private String name;
	private String description;
	
	public Material(String name, String description) {
		setName(name);
		setDescription(description);
	}
	
	@JsonIgnore
	@ManyToMany(mappedBy="materials")
	private List<Collection> collections = new ArrayList<>();
	

}
