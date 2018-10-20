package edu.buffalo.cse.wot.neo4j.utils;

import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.model.GraphNode;
import edu.buffalo.cse.wot.neo4j.model.UserNode;

/**
 * 
 * @author varunjai
 *
 */
public class GraphNodeUtils {

  /**
   * 
   * @param graphNode
   * @return
   */
  public static String getNodeLabel(GraphNode graphNode) {

    // empty
    if (graphNode == null) {
      return null;
    }

    if (graphNode instanceof UserNode) {
      return AppConstants.LABEL_USER;
    }

    return AppConstants.LABEL_QUESTION;
  }
}
