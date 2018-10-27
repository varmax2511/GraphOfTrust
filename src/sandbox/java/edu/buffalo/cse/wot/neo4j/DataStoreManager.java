package edu.buffalo.cse.wot.neo4j;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.buffalo.cse.wot.neo4j.config.AppConfiguration;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.UserNode;
import edu.buffalo.cse.wot.neo4j.utils.CacheManager;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;

public class DataStoreManager {

  private AppConfiguration appConfiguration;
  private final DataStore dataStore;
  private static Logger logger = LogManager.getLogger(DataStoreManager.class);

  /**
   * 
   * @param appConfiguration
   */
  public DataStoreManager(AppConfiguration appConfiguration) {
    Validate.notNull(appConfiguration);
    this.appConfiguration = appConfiguration;
    this.dataStore = Neo4jStore.getInstance();
    dataStore.startServer();
  }

  public void addNode(UserNode node) {
    dataStore.save(node);
  }

  public static void main(String[] args) throws InterruptedException {
    UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    Edge edge1 = new Edge(node1, node2, 1.0f - 0.8f);
    node1.addEdge(edge1);

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));
    node2.addEdge(new Edge(node2, node5, 1.0f - 1.0f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));

    node4.addEdge(new Edge(node4, node5, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node3, 1.0f - 0.4f));

    DataStoreManager dsm = new DataStoreManager(AppConfiguration.getInstance());
    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);

    final Map<String, Float> shortestPath = DijkstraAlgorithm
        .findShortedPathFrmSrc2(AppConstants.LABEL_USER, "1",
            Neo4jStore.getInstance());

    // find the shortest path
    float min = Float.MAX_VALUE;
    long minUid = -1;
    for (Map.Entry<String, Float> entry : shortestPath.entrySet()) {
      float val = entry.getValue();

      if (val < min) {
        min = val;
        minUid = Long.parseLong(entry.getKey());
      }
    } // for

    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(CacheManager.getInstance().getUids(), 0.75f);

    logger.info("Trust response: {}",
        distribution.getKey().indexOf(minUid) == -1 ? "YES" : "NO");

    Thread.sleep(100000000);
  }
}
