package edu.buffalo.cse.wot.neo4j.model;

import java.util.List;
import java.util.Map;

/**
 * Marker interface implemented by any model used to persist data in Neo4j.
 * 
 * @author varunjai
 *
 */
public interface GraphNode {

  /**
   * 
   * @param key
   * @return
   */
  public Object getPropertyByKey(String key);
  /**
   * 
   * @param key
   * @param value
   * @return
   */
  public void setProperty(String key, Object value);

  public Map<String, Object> getAllProperties();

  public List<Edge> getEdges();
  /**
   * 
   * @param edge
   */
  public void addEdge(Edge edge);
}
