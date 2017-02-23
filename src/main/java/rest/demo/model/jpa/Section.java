package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

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
public class Section extends AbstractEntity {
	
	public Section(String name, String slug) {
		this.setName(name);
		this.setSlug(slug);
	}
	
	private String name;
	private String slug;
	
	@JsonIgnore
	@OneToMany(mappedBy="section")
	private List<Area> areas = new ArrayList<>();
	
}
