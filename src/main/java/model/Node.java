/**
 * 
 */
package model;

import java.util.List;

/**
 * @author snehamehta
 *
 */
public class Node {
	private Long id; //PK
	private String name;
	private List<Long> inEdgeIds;
	private List<Long> outEdgeIds;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Long> getInEdgeIds() {
		return inEdgeIds;
	}
	public void setInEdgeIds(List<Long> inEdgeIds) {
		this.inEdgeIds = inEdgeIds;
	}
	public List<Long> getOutEdgeIds() {
		return outEdgeIds;
	}
	public void setOutEdgeIds(List<Long> outEdgeIds) {
		this.outEdgeIds = outEdgeIds;
	}
	
	
}
