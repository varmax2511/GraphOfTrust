package edu.buffalo.cse.wot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.databind.JsonNode;

import edu.buffalo.cse.wot.neo4j.JettyServer;
import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils.TRUST_DECAY_TYPE;

/**
 * 
 * @author varunjai
 *
 */
public class TestDijkstraSmallDense {

  private static JettyServer server;
  private static DataStoreManager dsm;
  private static Logger logger = LogManager
      .getLogger(TestDijkstraSmallSimple.class);
  static List<Long> uids;

  @BeforeClass
  public static void testPrep() throws Exception {
    server = new JettyServer();
    server.start();
    dsm = DataStoreManager.getInstance();
    
    // load simple graph
    Pair<List<Long>, JsonNode> pair = DataUtils.loadSmallDenseGraph(dsm);
    uids = pair.getKey();
  }

  @org.junit.Test
  public void testFixedDijkstra1() {

    long id = 3;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.75f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, String.valueOf(id),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @org.junit.Test
  public void testFixedDijkstra2() {

    long id = 3;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.5f, uids.size(), id, true);

    // find the shortest path
    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, String.valueOf(id),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  /**
   * 
   */
  @org.junit.Test
  public void testFixedDijkstra3() {

    long id = 2;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.25f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, String.valueOf(id),
        TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution).getResult());
  }

  @org.junit.Test
  public void testFixedDijkstra4() {
    long id = 8;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.1f, uids.size(), id, true);

    assertFalse(DataStoreManager.getInstance().getShortestStrongestResponse(
        AppConstants.LABEL_USER, String.valueOf(id),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }

}
