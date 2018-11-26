package edu.buffalo.cse.wot.neo4j.datastore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils.TRUST_DECAY_TYPE;

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

  /**
   * 
   * @param labelName
   * @param uid
   * @param neo4jStore
   * @return
   */
  public static Map<String, Float> findShortedPathFrmSrc(String labelName,
      String uid, TRUST_DECAY_TYPE trustDecayType, Neo4jStore neo4jStore) {

    // null check
    if (StringUtils.isBlank(labelName) || StringUtils.isBlank(uid)) {
      return new HashMap<>();
    }

    /*
     * Processing
     */
    final Map<String, Float> dist = new HashMap<>();
    try (Transaction tx = neo4jStore.getGraphDB().beginTx()) {
      final Node srcNode = neo4jStore.getNode(labelName, uid);

      // no node found
      if (srcNode == null) {
        throw new NoSuchElementException(
            "No node was found with Label: " + labelName + " and UID: " + uid);
      }

      final Set<String> visited = new HashSet<>();
      final Map<String, Container> unvisited = new HashMap<>();
      dist.put(uid, 0f);
      unvisited.put(uid, new Container(srcNode, 0, 1, 1));

      // visit every node
      while (unvisited.size() > 0) {
        final String minUid = minDistance(dist, unvisited);
        final Container nodeContainer = unvisited.get(minUid);
        unvisited.remove(minUid);
        visited.add(minUid);

        final Iterator<Relationship> itr = nodeContainer.getNode()
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

          float edgeWeight = (float) relationship
              .getProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT);

          final Container newNodeContainer = new Container(endNode,
              nodeContainer.getHops() + 1,
              nodeContainer.getEdgeWeightSum() + edgeWeight,
              nodeContainer.getEdgeWeightPrd() * edgeWeight == 0
                  ? 1
                  : edgeWeight);

          float cost = 0;
          // compute cost
          switch (trustDecayType) {
            case CUMULATIVE_TRUST_DECAY :
              cost = dist.get(minUid)
                  + (float) relationship
                      .getProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT)
                  + (float) TrustDecayUtils.cumulativeTrust2(
                      newNodeContainer.edgeWeightSum,
                      newNodeContainer.edgeWeightPrd,
                      newNodeContainer.getHops() + 1);
              break;
            case LOG_TRUST_DECAY :
              cost = dist.get(minUid)
                  + (float) relationship
                      .getProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT)
                  + (float) TrustDecayUtils
                      .logarithmicTrustDecay(newNodeContainer.getHops() + 1);
          }

          // if cost is less than already computed cost
          if (!dist.containsKey(endUid) || dist.get(endUid) > cost) {
            dist.put(endUid, cost);
          }

          unvisited.put(endUid, newNodeContainer);
        } // while

      } // while
    } // try

    return dist;
  }

  /**
   * Find the minimum distance node {@link AppConstants#NODE_UID}.
   * 
   * @param dist
   * @param unvisited
   * @return
   */
  private static String minDistance(Map<String, Float> dist,
      Map<String, Container> unvisited) {
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

  /**
   * Get the shortest path node uid which participated in the answering process,
   * responding in yay or nay.
   * 
   * @param uid2ShortestPaths
   * @param yayNnay
   * @return
   */
  public static long getShortestPathNodeUid(
      final Map<String, Float> uid2ShortestPaths,
      final Pair<Set<Long>, Set<Long>> yayNnay) {
    // validate
    if (uid2ShortestPaths == null || MapUtils.isEmpty(uid2ShortestPaths)) {
      return -1;
    }

    float min = Float.MAX_VALUE;
    long minUid = -1;
    for (Map.Entry<String, Float> entry : uid2ShortestPaths.entrySet()) {
      float val = entry.getValue();

      if (val >= min) {
        continue;
      }

      long uid = Long.parseLong(entry.getKey());
      // check if the node participated in answering the question
      // pair key represents nodes answering yes and value represents nodes
      // answering no
      if (!(yayNnay.getKey().contains(uid)
          || yayNnay.getValue().contains(uid))) {
        continue;
      }

      min = val;
      minUid = uid;

    } // for
    return minUid;
  }

  /**
   * Get the shortest strongest path from the source which participated in the
   * Q&A
   * 
   * @param uid2ShortestPaths
   *          !empty
   * @param yayNnay
   *          !empty
   * @return
   */
  public static boolean getShortestStrongestResponse(
      final Map<String, Float> uid2ShortestPaths,
      final Pair<Set<Long>, Set<Long>> yayNnay) {
    // validate
    if (uid2ShortestPaths == null || MapUtils.isEmpty(uid2ShortestPaths)) {
      throw new RuntimeException("No shortest path found");
    }

    float min = Float.MAX_VALUE;
    long minUid = -1;
    boolean minResponse = false;
    for (Map.Entry<String, Float> entry : uid2ShortestPaths.entrySet()) {
      float val = entry.getValue();

      if (val >= min) {
        continue;
      }

      long uid = Long.parseLong(entry.getKey());
      // check if the node participated in answering the question
      // pair key represents nodes answering yes and value represents nodes
      // answering no
      if (!(yayNnay.getKey().contains(uid)
          || yayNnay.getValue().contains(uid))) {
        continue;
      }

      min = val;
      minUid = uid;
      minResponse = yayNnay.getKey().contains(minUid) ? true : false;

    } // for
    return minResponse;
  }

  /**
   * 
   * @author varunjai
   *
   */
  static class Container {
    private final Node node;
    private final int hops;
    private final float edgeWeightSum;
    private final float edgeWeightPrd;

    public Container(Node node, int hops, float edgeWeightSum,
        float edgeWeightPrd) {
      this.node = node;
      this.hops = hops;
      this.edgeWeightSum = edgeWeightSum;
      this.edgeWeightPrd = edgeWeightPrd;
    }

    public Node getNode() {
      return node;
    }

    public int getHops() {
      return hops;
    }

    public float getEdgeWeightSum() {
      return edgeWeightSum;
    }

    public float getEdgeWeightPrd() {
      return edgeWeightPrd;
    }

  }
}
