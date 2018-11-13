package edu.buffalo.cse.wot.neo4j.datastore;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.buffalo.cse.wot.neo4j.DataStore;
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

  public long getShortestPathUidFromSrc(String label, String id) {
    return DijkstraAlgorithm.getShortestPathNodeUid(DijkstraAlgorithm
        .findShortedPathFrmSrc(label, id, Neo4jStore.getInstance()));
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

}
