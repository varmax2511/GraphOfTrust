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
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils.TRUST_DECAY_TYPE;

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
    final List<List<String>> sccs = new ArrayList<>();
    for (int i = 1; i <= numVertices; i++) {
      if (!disc.containsKey(String.valueOf(i))) {
        getSCCUtil(String.valueOf(i), disc, low, stack, stackMember, labelName,
            neo4jStore, sccs);
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
  public static void getSCCUtil(String uid, Map<String, Integer> disc,
      Map<String, Integer> low, Stack<String> stack,
      Map<String, Boolean> stackMember, String labelName, Neo4jStore neo4jStore,
      List<List<String>> sccs) {

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
          getSCCUtil(v, disc, low, stack, stackMember, labelName, neo4jStore,
              sccs);
          low.put(uid, Math.min(low.get(uid), low.get(v)));
        } else if (stackMember.get(v)) {
          low.put(uid, Math.min(low.get(uid), disc.get(v)));
        }
      } // while

      String w;
      if (low.get(uid) == disc.get(uid)) {
        while (!stack.peek().equals(uid)) {
          w = stack.peek();
          scc.add(w);
          stackMember.put(w, false);
          stack.pop();
        } // while

        w = stack.peek();
        scc.add(w);
        stackMember.put(w, false);
        stack.pop();
      } // if
    } // try

    if (!CollectionUtils.isEmpty(scc)) {
      sccs.add(scc);
    }
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
  public static TrustOutput computeResponse(String label, String uid,
      TRUST_DECAY_TYPE trustDecayType, final List<List<String>> sccsUids,
      Neo4jStore neo4jStore, final Pair<Set<Long>, Set<Long>> yayNnay) {

    final Map<String, Float> shortesPaths = DijkstraAlgorithm
        .findShortedPathFrmSrc(label, uid, trustDecayType, neo4jStore);

    final Map<String, Integer> sccUids2Response = new HashMap<>();

    for (final List<String> sccUids : sccsUids) {
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
    for (final Map.Entry<String, Integer> entry : sccUids2Response.entrySet()) {
      // SCC couldn't reach a decision
      if (entry.getValue() == 0) {
        continue;
      }
      final float trust = shortesPaths.containsKey(entry.getKey())
          ? shortesPaths.get(entry.getKey())
          : 0.3f;
      if (entry.getValue() > 0) {
        yayTrust += trust / Math.sqrt(entry.getValue());
        yayCnt++;
      } else {
        nayTrust += trust / (Math.sqrt(-1 * entry.getValue()));
        nayCnt++;
      }
    } // for

    
    double yes = (yayTrust * yayCnt) / ((yayTrust * yayCnt) + (nayTrust * nayCnt));
    double no = (nayTrust * nayCnt) / ((yayTrust * yayCnt) + (nayTrust * nayCnt));
    
    final TrustOutput trustOutput = new TrustOutput();
    trustOutput.setResult(yes > no ? true : false);
    
    trustOutput.setConfidence(
        trustOutput.getResult() ? yes : no);
    trustOutput
        .setHeuristic(AppConstants.STRONGLY_CONNECTED_COMPONENTS_HEURISTIC);
    trustOutput.setTrustDecayType(trustDecayType.toString());
    trustOutput.setYesIds(yayNnay.getKey());
    trustOutput.setNoIds(yayNnay.getValue());
    
    return trustOutput;
  }

  /**
   *
   * @param sccUids
   * @param yayNnay
   */
  private static int computeSccResponse(List<String> sccUids,
      final Pair<Set<Long>, Set<Long>> yayNnay) {

    int yes = 0;
    int no = 0;
    for (final String sUid : sccUids) {
      final long uid = Long.parseLong(sUid);
      if (yayNnay.getKey().contains(uid)) {
        yes++;
      } else if (yayNnay.getValue().contains(uid)) {
        no++;
      }
    } // for

    if (yes == 0 && no == 0) {
      return 0;
    }
    return yes > no ? 1 * sccUids.size() : -1 * sccUids.size();
  }
}
