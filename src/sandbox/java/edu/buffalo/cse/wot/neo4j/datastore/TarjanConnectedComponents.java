package edu.buffalo.cse.wot.neo4j.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.collections4.CollectionUtils;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;

/**
 * <pre>
 * reference:
 * <a href=
'https://www.geeksforgeeks.org/tarjan-algorithm-find-strongly-connected-components/'>Tarjan's algorithm</a>
 * </pre>
 * 
 * @author varunjai
 *
 */
public class TarjanConnectedComponents {

  static int time;
  /**
   * 
   * @param numVertices
   * @param labelName
   * @param neo4jStore
   */
  public static List<List<String>> stronglyConnectedComponents(int numVertices,
      String labelName, Neo4jStore neo4jStore) {

    final Map<String, Integer> disc = new HashMap<>();
    final Map<String, Integer> low = new HashMap<>();
    final Stack<String> stack = new Stack<>();
    final Map<String, Boolean> stackMember = new HashMap<>();

    time = 0;
    List<List<String>> sccs = new ArrayList<>();
    for (int i = 1; i <= numVertices; i++) {
      if (!disc.containsKey(String.valueOf(i))) {
        List<String> scc = getSCCUtil(String.valueOf(i), disc, low, stack,
            stackMember, labelName, neo4jStore);

        if (!CollectionUtils.isEmpty(scc)) {
          sccs.add(scc);
        }
      }
    } // for
    time = 0;

    return sccs;
  }

  /**
   * 
   * @param uid
   * @param disc
   * @param low
   * @param stack
   * @param stackMember
   * @param labelName
   * @param neo4jStore
   */
  public static List<String> getSCCUtil(String uid, Map<String, Integer> disc,
      Map<String, Integer> low, Stack<String> stack,
      Map<String, Boolean> stackMember, String labelName,
      Neo4jStore neo4jStore) {

    final List<String> scc = new ArrayList<>();
    disc.put(uid, ++time);
    low.put(uid, time);

    stack.push(uid);
    stackMember.put(uid, true);

    try (Transaction tx = neo4jStore.getGraphDB().beginTx()) {
      final Node node = neo4jStore.getNode(labelName, uid);

      // no node found
      if (node == null) {
        throw new NoSuchElementException(
            "No node was found with Label: " + labelName + " and UID: " + uid);
      }

      final Iterator<Relationship> itr = node
          .getRelationships(Direction.OUTGOING).iterator();

      while (itr.hasNext()) {
        final Relationship relationship = itr.next();
        final Node endNode = relationship.getEndNode();

        final String v = endNode.getProperty(AppConstants.NODE_UID).toString();

        if (!disc.containsKey(v)) {
          getSCCUtil(v, disc, low, stack, stackMember, labelName, neo4jStore);
          low.put(uid, Math.min(low.get(uid), low.get(v)));
        } else if (stackMember.get(v)) {
          low.put(uid, Math.min(low.get(uid), disc.get(v)));
        }
      } // while

      String w;
      if (low.get(uid) == disc.get(uid)) {
        System.out.println();
        while (!stack.peek().equals(uid)) {
          w = stack.peek();
          scc.add(w);
          System.out.print(w);
          stackMember.put(w, false);
          stack.pop();
        } // while

        w = stack.peek();
        System.out.print(w);
        scc.add(w);
        stackMember.put(w, false);
        stack.pop();
      } // if
    } // try

    return scc;
  }

  /**
   * The idea is to compute the weighted average response of each Strongly
   * connected component (SCC) based on trust and of the nodes which
   * participated in the Q&A. Once we have the trust weighted response from each
   * SCC, we can compute the average response from all these SCC's to reach to a
   * final answer. The concept of using SCC's is that people tend to trust
   * people with whom they are strongly connected rather than with people with
   * whom they have mere acquaintances.
   * 
   * @param sccUids
   */
  public static boolean computeResponse(String label, String uid,
      final List<List<String>> sccsUids, Neo4jStore neo4jStore,
      final Pair<Set<Long>, Set<Long>> yayNnay) {

    final Map<String, Float> shortesPaths = DijkstraAlgorithm
        .findShortedPathFrmSrc(label, uid, neo4jStore);

    final Map<String, Boolean> sccUids2Response = new HashMap<>();

    for (List<String> sccUids : sccsUids) {
      // empty
      if (CollectionUtils.isEmpty(sccUids)) {
        continue;
      }

      sccUids2Response.put(sccUids.get(0),
          computeSccResponse(sccUids, yayNnay));
    } // for

    float yayTrust = 0;
    int yayCnt = 0;
    float nayTrust = 0;
    int nayCnt = 0;

    /*
     * Compute the weighted average of yay sayers and weighted average of nay
     * sayers.
     */
    for (Map.Entry<String, Boolean> entry : sccUids2Response.entrySet()) {
      float trust = shortesPaths.containsKey(entry.getKey())
          ? shortesPaths.get(entry.getKey())
          : 0.2f;
      if (entry.getValue()) {
        yayTrust += trust;
        yayCnt++;
      } else {
        nayTrust += trust;
        nayCnt++;
      }
    } // for

    return (yayTrust / yayCnt) > (nayTrust / nayCnt);
  }

  /**
   * 
   * @param sccUids
   * @param yayNnay
   */
  private static boolean computeSccResponse(List<String> sccUids,
      final Pair<Set<Long>, Set<Long>> yayNnay) {

    int yes = 0;
    int no = 0;
    for (String uid : sccUids) {
      if (yayNnay.getKey().contains(uid)) {
        yes++;
      } else if (yayNnay.getValue().contains(uid)) {
        no++;
      }
    } // for

    return yes > no ? true : false;
  }
}
