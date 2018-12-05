package edu.buffalo.cse.wot.neo4j.utils;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.GraphNode;
import edu.buffalo.cse.wot.neo4j.model.UserNode;

/**
 * 
 * @author varunjai
 *
 */
public class JsonNodeUtils {

  /**
   * 
   * @param graphNodes
   * @return
   */
  public static JsonNode getJsonGraph(List<GraphNode> graphNodes) {
    ObjectNode output = JsonNodeFactory.instance.objectNode();
    ArrayNode nodes = JsonNodeFactory.instance.arrayNode();
    ArrayNode links = JsonNodeFactory.instance.arrayNode();

    for (GraphNode node : graphNodes) {
      ObjectNode object = JsonNodeFactory.instance.objectNode();

      object.set("id", JsonNodeFactory.instance.numberNode(Long
          .parseLong(node.getPropertyByKey(AppConstants.NODE_UID).toString())));
      object.set("name", JsonNodeFactory.instance
          .textNode(node.getPropertyByKey("name").toString()));

      nodes.add(object);

      for (Edge edge : node.getEdges()) {
        ObjectNode link = JsonNodeFactory.instance.objectNode();

        link.set("source",
            JsonNodeFactory.instance
                .numberNode(Long.parseLong(edge.getStartNode()
                    .getPropertyByKey(AppConstants.NODE_UID).toString())));
        link.set("target",
            JsonNodeFactory.instance.numberNode(Long.parseLong(edge.getEndNode()
                .getPropertyByKey(AppConstants.NODE_UID).toString())));

        link.set("weight",
            JsonNodeFactory.instance.numberNode(1- edge.getWeight()));

        links.add(link);
      } // for

    } // for

    
    output.set("nodes", nodes);
    output.set("links", links);
    return output;
  }

  public static void main(String[] args) {
    final UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    final UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    final UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    final UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    final UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    final Edge edge1 = new Edge(node1, node2, 1.0f - 0.8f);
    node1.addEdge(edge1);

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));
    node2.addEdge(new Edge(node2, node5, 1.0f - 1.0f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));

    node4.addEdge(new Edge(node4, node5, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node3, 1.0f - 0.4f));

    List<GraphNode> graphNodes = new ArrayList<>();
    graphNodes.add(node1);
    graphNodes.add(node2);
    graphNodes.add(node3);
    graphNodes.add(node4);
    graphNodes.add(node5);

    getJsonGraph(graphNodes);
  }
}
