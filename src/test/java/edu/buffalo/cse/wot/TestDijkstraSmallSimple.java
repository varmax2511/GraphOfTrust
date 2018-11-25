package edu.buffalo.cse.wot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import edu.buffalo.cse.wot.neo4j.JettyServer;
import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;
/**
 * 
 * @author varunjai
 *
 */
public class TestDijkstraSmallSimple {

  private static JettyServer server;
  private static Logger logger = LogManager.getLogger(TestDijkstraSmallSimple.class);
  static List<Long> uids;

  @BeforeClass
  public static void testPrep() throws Exception {
    server = new JettyServer();
    server.start();

    // load simple graph
    uids = DataUtils.loadSmallGraph();
  }

  @org.junit.Test
  public void testFixedDijkstra1() {

    DataStoreManager.getInstance().getSccUids(AppConstants.LABEL_USER,
        uids.size());

    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.75f, uids.size());

    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, "1", distribution));
  }

  @org.junit.Test
  public void testFixedDijkstra2() {

    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.5f, uids.size());

    // find the shortest path
    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, "1", distribution));
  }

  /**
   * 
   */
  @org.junit.Test
  public void testFixedDijkstra3() {

    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.25f, uids.size());

    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, "1", distribution));
  }

  /**
   * 
   */
  @org.junit.Test
  public void testFixedDijkstra4() {

    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.1f, uids.size());

    assertFalse(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, "1", distribution));
  }

  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }
}
