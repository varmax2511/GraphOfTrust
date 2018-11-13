package edu.buffalo.cse.wot;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import edu.buffalo.cse.wot.neo4j.JettyServer;
import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.UserNode;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;
/**
 * 
 * @author varunjai
 *
 */
public class Test {

  private static JettyServer server;
  private static Logger logger = LogManager.getLogger(Test.class);
  final static List<Long> uids = new ArrayList<Long>();

  @BeforeClass
  public static void testPrep() throws Exception {
    server = new JettyServer();
    server.start();

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

    final DataStoreManager dsm = DataStoreManager.getInstance();
    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);

    uids.add(1l);
    uids.add(2l);
    uids.add(3l);
    uids.add(4l);
    uids.add(5l);

  }
  
  @org.junit.Test
  public void testSmallDijkstra1() {

    // find the shortest path
    final long minUid = DataStoreManager.getInstance()
        .getShortestPathUidFromSrc(AppConstants.LABEL_USER, "1");

    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(uids, 0.75f);

    assertEquals(distribution.getKey().indexOf(minUid), -1);
  }

  @org.junit.Test
  public void testSmallDijkstra2() {

    // find the shortest path
    final long minUid = DataStoreManager.getInstance()
        .getShortestPathUidFromSrc(AppConstants.LABEL_USER, "1");

    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(uids, 0.5f);

    assertEquals(distribution.getKey().indexOf(minUid), -1);
  }

  
  @org.junit.Test
  public void testSmallDijkstra3() {

    // find the shortest path
    final long minUid = DataStoreManager.getInstance()
        .getShortestPathUidFromSrc(AppConstants.LABEL_USER, "1");

    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(uids, 0.25f);

    assertEquals(distribution.getKey().indexOf(minUid), -1);
  }

  
  @org.junit.Test
  public void testSmallDijkstra4() {

    // find the shortest path
    final long minUid = DataStoreManager.getInstance()
        .getShortestPathUidFromSrc(AppConstants.LABEL_USER, "1");

    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(uids, 0.1f);

    assertEquals(distribution.getKey().indexOf(minUid), -1);
  }

  
  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }
}
