package edu.buffalo.cse.wot.neo4j.datastore;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import edu.buffalo.cse.wot.neo4j.DataStore;
import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.model.UserNode;

/**
 * 
 * @author varunjai
 *
 */
public class DataStoreManager {

  private final DataStore dataStore;
  private static Logger logger = LogManager.getLogger(DataStoreManager.class);

  /**
   * 
   * @param appConfiguration
   */
  public DataStoreManager() {
    this.dataStore = Neo4jStore.getInstance();
    dataStore.startServer();
  }

  /**
   * 
   * @author varunjai
   *
   */
  private static class LazyHolder {
    private static final DataStoreManager INSTANCE = new DataStoreManager();
  }

  /**
   * 
   * @return
   */
  public static DataStoreManager getInstance() {
    return LazyHolder.INSTANCE;
  }

  /**
   * Add a node
   * 
   * @param node
   */
  public void addNode(UserNode node) {
    dataStore.save(node);
  }

  /**
   * Get the shortest paths to every node connected to node with given id in the
   * Label.
   * 
   * @param label
   *          !empty
   * @param id
   *          !empty
   * @return
   */
  public Map<String, Float> findShortedPathFrmSrc(String label, String id) {
    return DijkstraAlgorithm.findShortedPathFrmSrc(label, id,
        Neo4jStore.getInstance());
  }

  /**
   * 
   * @param label
   * @param id
   * @param yayNnay
   * @return
   */
  public long getShortestPathUidFromSrc(String label, String id,
      final Pair<Set<Long>, Set<Long>> yayNnay) {
    return DijkstraAlgorithm.getShortestPathNodeUid(DijkstraAlgorithm
        .findShortedPathFrmSrc(label, id, Neo4jStore.getInstance()), yayNnay);
  }

  /**
   * 
   * @param label
   * @param id
   * @param yayNnay
   * @return
   */
  public boolean getShortestStrongestResponse(String label, String id,
      final Pair<Set<Long>, Set<Long>> yayNnay) {
    return DijkstraAlgorithm.getShortestStrongestResponse(DijkstraAlgorithm
        .findShortedPathFrmSrc(label, id, Neo4jStore.getInstance()), yayNnay);
  }

  /**
   * 
   * @param label
   * @param numVertices
   * @return
   */
  public List<List<String>> getSccUids(String label, int numVertices) {
    return TarjanConnectedComponents.stronglyConnectedComponents(numVertices,
        label, Neo4jStore.getInstance());
  }

  /**
   * 
   * @param label
   * @param id
   * @param numVertices
   * @param yayNnay
   * @return
   */
  public boolean getResponseFrmSCC(String label, String id, int numVertices,
      final Pair<Set<Long>, Set<Long>> yayNnay) {
    return TarjanConnectedComponents.computeResponse(label, id,
        TarjanConnectedComponents.stronglyConnectedComponents(numVertices,
            label, Neo4jStore.getInstance()),
        Neo4jStore.getInstance(), yayNnay);

  }

  /**
   * Stop the process
   */
  public void stop() {
    if (this.dataStore == null) {
      return;
    }
    logger.info("Stopping Database");
    this.dataStore.stopServer();
  }
  
  /**
   * 
   * @param labelName
   * @param feedback
   * @param uids
   */
  public static void assignFeedback(String labelName, boolean feedback,
      Set<Long> uids) {
    final Neo4jStore neo4jStore = Neo4jStore.getInstance();
    for (Long uid : uids) {
      try (Transaction tx = neo4jStore.getGraphDB().beginTx()) {
        final Node node = neo4jStore.getNode(labelName, String.valueOf(uid));
  
        final Iterator<Relationship> itr = node
            .getRelationships(Direction.INCOMING).iterator();
  
        while (itr.hasNext()) {
          Relationship relationship = itr.next();
          float edgeWeight = (float) relationship
              .getProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT);
  
          if (feedback && edgeWeight - AppConstants.FEEDBACK_TRUST_REWARD > 0) {
            edgeWeight = edgeWeight - AppConstants.FEEDBACK_TRUST_REWARD;
          } else if (!feedback && edgeWeight + AppConstants.FEEDBACK_TRUST_REWARD < 1) {
            edgeWeight = edgeWeight + AppConstants.FEEDBACK_TRUST_REWARD;
          }
          relationship.setProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT,
              edgeWeight);
        } // while
        tx.success();
      } // try
  
    } // for
  }

  public void reset() {
    if (this.dataStore == null) {
      return;
    }
    logger.info("Stopping Database");
    this.dataStore.stopServer();
    logger.info("restarting database....");
    this.dataStore.startServer();
    logger.info("database restarted");
  }

}
