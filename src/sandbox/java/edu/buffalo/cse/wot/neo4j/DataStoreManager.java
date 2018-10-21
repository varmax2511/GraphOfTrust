package edu.buffalo.cse.wot.neo4j;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.buffalo.cse.wot.neo4j.config.AppConfiguration;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.UserNode;

public class DataStoreManager {

  private AppConfiguration appConfiguration;
  private final DataStore dataStore;
  private static Logger logger = LoggerFactory
      .getLogger(DataStoreManager.class);

  /**
   * 
   * @param appConfiguration
   */
  public DataStoreManager(AppConfiguration appConfiguration) {
    Validate.notNull(appConfiguration);
    this.appConfiguration = appConfiguration;
    this.dataStore = new Neo4jStore(appConfiguration);
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

    Edge edge1 = new Edge(node1, node2, 0.8f);
    node1.addEdge(edge1);

    node2.addEdge(new Edge(node2, node3, 0.2f));
    node2.addEdge(new Edge(node2, node5, 1.0f));

    node3.addEdge(new Edge(node3, node4, 0.5f));

    node4.addEdge(new Edge(node4, node5, 0.7f));

    node5.addEdge(new Edge(node5, node3, 0.4f));

    DataStoreManager dsm = new DataStoreManager(new AppConfiguration());
    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);
    
    
    Thread.sleep(100000000);
  }
}
