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

public class TestTarjanSmallSimple {

  private static JettyServer server;
  private static Logger logger = LogManager
      .getLogger(TestDijkstraSmallSimple.class);
  private static List<Long> uids;
  private static DataStoreManager dsm;

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
  public void testFixedTarjan1() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.75f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @org.junit.Test
  public void testFixedTarjan2() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.5f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @org.junit.Test
  public void testFixedTarjan3() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.25f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @org.junit.Test
  public void testFixedTarjan4() {

    final long id = 1;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.1f, uids.size(), id, true);

    assertFalse(DataStoreManager.getInstance().getResponseFrmSCC(
        AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
        TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution).getResult());
  }

  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }
}
