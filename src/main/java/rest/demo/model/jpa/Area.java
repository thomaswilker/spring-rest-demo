package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class Area extends JpaEntity {

	
	public Area(String name, Section section) {
		this.setName(name);
		this.setSection(section);
	}
	
	public Area(String name) {
		setName(name);
	}
	
	@Size(max=200, message="Maximale Anzahl an Zeichen")
	@NotNull
	private String name;
	
	@ManyToOne(optional=false)
	@JoinColumn(nullable=false)
	private Section section;
	
	@ManyToMany(mappedBy="areas")
	@Getter(onMethod = @__( @JsonIgnore ))
	@ReIndex
	private List<Collection> collections = new ArrayList<>();
	
}
