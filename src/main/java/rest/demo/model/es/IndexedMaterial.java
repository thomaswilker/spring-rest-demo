package rest.demo.model.es;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.model.es.IndexedCollection.IndexedCollectionReduced;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "materials", type = "material" , shards = 1, replicas = 1, indexStoreType = "fs", refreshInterval = "-1")
@JsonIgnoreProperties(ignoreUnknown=true)
public class IndexedMaterial extends IndexedEntity {

	private String name;
	private String description;
	private List<IndexedCollectionReduced> collections;
	
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown=true)
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IndexedMaterialReduced extends IndexedEntity {
		
		private String name;
		public IndexedMaterialReduced(Long id, String name) {
			setId(id);
			setName(name);
		}
		
	}
	
	
}
