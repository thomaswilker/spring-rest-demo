package rest.demo.model.es;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import rest.demo.model.es.IndexedMaterial.IndexedMaterialReduced;

@Getter
@Setter
@NoArgsConstructor
@Document(indexName = "collections", type = "collection" , shards = 1, replicas = 1, indexStoreType = "fs", refreshInterval = "-1")
@JsonIgnoreProperties(ignoreUnknown=true)
public class IndexedCollection extends IndexedEntity {

	@Field(type=FieldType.String)
	private String name;
	
	private List<IndexedArea> areas = new ArrayList<>();
	
	private List<IndexedMaterial> materials = new ArrayList<>();

	
	@Getter
	@Setter
	@JsonIgnoreProperties(ignoreUnknown=true)
	@NoArgsConstructor
	public static class IndexedCollectionReduced extends IndexedEntity {
		private String name;
		public IndexedCollectionReduced(Long id, String name, IndexedMaterialReduced nextLecture) {
			setId(id);
			setName(name);
			setNextLecture(nextLecture);
		}
		private IndexedMaterialReduced nextLecture;
	}
}
