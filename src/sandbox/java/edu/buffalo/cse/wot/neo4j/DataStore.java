package edu.buffalo.cse.wot.neo4j;

import org.neo4j.graphdb.Node;

import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.GraphNode;

/**
 * 
 * @author varunjai
 *
 */
public interface DataStore {
  /**
   * Start the server
   */
  public void startServer();
  /**
   * Stop the server gracefully. <br/>
   * <b>Note: </b> The data directory will be deleted.
   */
  public void stopServer();

  /**
   * Convert {@link GraphNode} to {@link Node} and persist it. This method also
   * creates all relationships provided in {@link Edge}
   * 
   * @param graphNode
   */
  public void save(GraphNode graphNode);

  /**
   * Get the {@link Node} using the Unique identifier for the node.
   * 
   * @param labelName
   *          !blank
   * @param uid
   *          !blank
   * @return {@link Node} if node is found, else null.
   */
  public Node getNode(String labelName, String uid);
}
