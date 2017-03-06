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
import rest.demo.annotation.PropertySelect;
import rest.demo.annotation.ReIndex;
import rest.demo.annotation.PropertySelect.SelectType;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Area extends JpaEntity {

	
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
	@Getter(onMethod = @__( @JsonIgnore ))
	@ReIndex
	private List<Collection> collections = new ArrayList<>();
	
}
