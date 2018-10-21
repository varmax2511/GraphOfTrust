package edu.buffalo.cse.wot.neo4j.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;

/**
 * 
 * @author varunjai
 *
 */
public class UserNode implements GraphNode {

  private final Map<String, Object> properties = new HashMap<>();
  private final List<Edge> edges = new ArrayList<>();
  /**
   * 
   * @param key
   * @return
   */
  public Object getPropertyByKey(String key) {
    Validate.notBlank(key);
    return properties.get(key);
  }
  /**
   * 
   * @param key
   * @param value
   * @return
   */
  public void setProperty(String key, Object value) {
    Validate.notBlank(key);
    if (value == null) {
      return;
    }
    properties.put(key, value);
  }

  public Map<String, Object> getAllProperties() {
    return properties;
  }

  public List<Edge> getEdges() {
    return this.edges;
  }

  /**
   * 
   * @param edge
   */
  public void addEdge(Edge edge) {
    if (edge == null) {
      return;
    }
    edges.add(edge);
  }

}
