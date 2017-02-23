package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
public class Area extends AbstractEntity {

	
	public Area(String name, Section section) {
		this.setName(name);
		this.setSection(section);
	}
	
	public Area(String name) {
		setName(name);
	}
	
	private String name;
	
	@ManyToOne(optional=false)
	@JoinColumn(nullable=false)
	private Section section;
	
	@ManyToMany(mappedBy="areas")
	@JsonIgnore
	private List<Collection> collections = new ArrayList<>();
	
}
