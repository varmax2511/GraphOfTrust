package edu.buffalo.cse.wot.neo4j.model;

import org.apache.commons.lang3.Validate;

/**
 *
 * @author varunjai
 *
 */
public class Edge {
  private long edgeId;
  private GraphNode startNode;
  private GraphNode endNode;
  private float weight;

  /**
   * 
   */
  public Edge() {
    // TODO Auto-generated constructor stub
  }

  /**
   *
   * @param startNode
   * @param endNode
   */
  public Edge(UserNode startNode, UserNode endNode) {
    // validate
    Validate.notNull(startNode);
    Validate.notNull(endNode);

    this.startNode = startNode;
    this.endNode = endNode;
    this.weight = 1;
  }

  /**
   *
   * @param startNode
   * @param endNode
   * @param weight
   */
  public Edge(UserNode startNode, UserNode endNode, float weight) {
    // validate
    Validate.notNull(startNode);
    Validate.notNull(endNode);

    this.startNode = startNode;
    this.endNode = endNode;
    this.weight = weight;
  }

  public long getEdgeId() {
    return this.edgeId;
  }
  public void setEdgeId(long edgeId) {
    this.edgeId = edgeId;
  }
  public GraphNode getStartNode() {
    return startNode;
  }
  public void setStartNode(UserNode startNode) {
    this.startNode = startNode;
  }
  public GraphNode getEndNode() {
    return endNode;
  }
  public void setEndNode(UserNode endNode) {
    this.endNode = endNode;
  }

  public float getWeight() {
    return weight;
  }

  public void setWeight(float weight) {
    this.weight = weight;
  }

}
