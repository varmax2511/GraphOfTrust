package edu.buffalo.cse.wot.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphalgo.WeightedPath;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils;

/**
 * Given a start node, perform a topological sort of the
 * {@link AppConstants#NODE_UID}. Once that is done, perform a Dijkstra algo run
 * where {@link Node} are brought in memory only when needed.
 *
 * @author varunjai
 *
 */
public class DijkstraAlgorithm {

  private static Logger logger = LogManager.getLogger(DijkstraAlgorithm.class);

  public static void findShortedPathFrmSrc(Node src) {

    final PathFinder<WeightedPath> finder = GraphAlgoFactory.dijkstra(
        PathExpanders.forTypeAndDirection(DynamicRelationshipType
            .withName(AppConstants.RELATIONSHIP_TYPE_EDGE), Direction.BOTH),
        AppConstants.RELATIONSHIP_EDGE_WEIGHT);

  }

  /**
   * 
   * @param labelName
   * @param uid
   * @param neo4jStore
   * @return
   */
  public static Map<String, Float> findShortedPathFrmSrc2(String labelName,
      String uid, Neo4jStore neo4jStore) {

    final Map<String, Float> dist = new HashMap<>();
    try (Transaction tx = neo4jStore.getGraphDB().beginTx()) {
      final Node srcNode = neo4jStore.getNode(labelName, uid);

      // no node found
      if (srcNode == null) {
        throw new NoSuchElementException(
            "No node was found with Label: " + labelName + " and UID: " + uid);
      }

      final Set<String> visited = new HashSet<>();
      final Map<String, Pair<Node, Integer>> unvisited = new HashMap<>();
      dist.put(uid, 0f);
      unvisited.put(uid, new Pair<Node, Integer>(srcNode, 0));

      // visit every node
      while (unvisited.size() > 0) {
        final String minUid = minDistance(dist, unvisited);
        final Pair<Node, Integer> nodeNhop = unvisited.get(minUid);
        unvisited.remove(minUid);
        visited.add(minUid);

        final Iterator<Relationship> itr = nodeNhop.getKey()
            .getRelationships(Direction.OUTGOING).iterator();

        // visit its adjacency
        while (itr.hasNext()) {
          final Relationship relationship = itr.next();
          final Node endNode = relationship.getEndNode();

          final String endUid = endNode.getProperty(AppConstants.NODE_UID)
              .toString();

          // check if already visited
          if (visited.contains(endUid)) {
            continue;
          }

          // compute cost
          final float cost = dist.get(minUid)
              + (float) relationship
                  .getProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT)
              + (float) TrustDecayUtils.logarithmicTrustDecay(nodeNhop.getValue() + 1);

          // if cost is less than already computed cost
          if (!dist.containsKey(endUid) || dist.get(endUid) > cost) {
            dist.put(endUid, cost);
          }

          unvisited.put(endUid,
              new Pair<Node, Integer>(endNode, nodeNhop.getValue() + 1));
        } // while

      } // while
    } // try

    return dist;
  }

  private static String minDistance(Map<String, Float> dist,
      Map<String, Pair<Node, Integer>> unvisited) {
    float min = Float.MAX_VALUE;
    String minUid = StringUtils.EMPTY;

    for (final Map.Entry<String, Float> entry : dist.entrySet()) {
      if (!unvisited.containsKey(entry.getKey())) {
        continue;
      }

      if (entry.getValue() < min) {
        min = entry.getValue();
        minUid = entry.getKey();
      }
    }

    return minUid;
  }
  /**
   *
   * @param node
   * @param stack
   * @param visited
   */
  public static void topologicalSort(Node node, Stack<String> stack,
      Map<String, Boolean> visited) {

    visited.put(node.getProperty(AppConstants.NODE_UID).toString(), true);

    final Iterator<Relationship> itr = node.getRelationships(Direction.OUTGOING)
        .iterator();

    while (itr.hasNext()) {
      final Relationship relationship = itr.next();
      final Node endNode = relationship.getEndNode();
      if (!visited
          .containsKey(endNode.getProperty(AppConstants.NODE_UID).toString())) {
        topologicalSort(endNode, stack, visited);
      }
    } // while

    stack.push(node.getProperty(AppConstants.NODE_UID).toString());
  }

}
