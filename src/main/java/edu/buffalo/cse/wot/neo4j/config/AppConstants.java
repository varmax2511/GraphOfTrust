package edu.buffalo.cse.wot.neo4j.config;

/**
 * 
 * @author varunjai
 *
 */
public interface AppConstants {

  public String LABEL_USER = "USER";
  public String LABEL_QUESTION = "QUESTION";
  public String NODE_UID = "uid";
  public String RELATIONSHIP_EDGE_WEIGHT = "weight";
  public String RELATIONSHIP_TYPE_EDGE = "EDGE";
  public float FEEDBACK_TRUST_REWARD = 0.005f;
  public String STRONGLY_CONNECTED_COMPONENTS_HEURISTIC = "Strongly Connected Components";
  public String SHORTEST_STRONGEST_PATH_HEURISTIC = "Shortest Strongest Path";
  

}
