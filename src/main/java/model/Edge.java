/**
 * 
 */
package model;

/**
 * @author snehamehta
 *
 */
public class Edge {
	private Long id;
	private Float weight;
	private Long sourceId;
	private Long destinationId;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Float getWeight() {
		return weight;
	}
	public void setWeight(Float weight) {
		this.weight = weight;
	}
	public Long getSourceId() {
		return sourceId;
	}
	public void setSourceId(Long sourceId) {
		this.sourceId = sourceId;
	}
	public Long getDestinationId() {
		return destinationId;
	}
	public void setDestinationId(Long destinationId) {
		this.destinationId = destinationId;
	}

}
