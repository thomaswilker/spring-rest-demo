package rest.demo.model.es;

import javax.persistence.Id;

import lombok.Data;

@Data
abstract class IndexedEntity {

	@Id
	private Long id;
}
