package rest.demo.model.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

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
public class Section extends JpaEntity {
	
	public Section(String name, String slug) {
		this.setName(name);
		this.setSlug(slug);
	}
	
	private String name;
	private String slug;
	
	@Getter(onMethod = @__( @JsonIgnore ))
	@OneToMany(fetch=FetchType.LAZY,mappedBy="section")
	@ReIndex(includePaths="this.collections", conditional=@PropertySelect(type=SelectType.PICK, properties="name"))
	private List<Area> areas = new ArrayList<>();
	
}
