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

public class TestTarjanSmallSimple {

  private static JettyServer server;
  private static Logger logger = LogManager
      .getLogger(TestDijkstraSmallSimple.class);
  static List<Long> uids;

  @BeforeClass
  public static void testPrep() throws Exception {
    server = new JettyServer();
    server.start();

    // load simple graph
    uids = DataUtils.loadSmallGraph();
  }

  @org.junit.Test
  public void testFixedTarjan1() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.75f, uids.size(), id);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        distribution));
  }
  
  @org.junit.Test
  public void testFixedTarjan2() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.5f, uids.size(), id);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        distribution));
  }

  @org.junit.Test
  public void testFixedTarjan3() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.25f, uids.size(), id);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        distribution));
  }
  
  @org.junit.Test
  public void testFixedTarjan4() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.1f, uids.size(), id);

    assertFalse(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        distribution));
  }

  
  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }
}
