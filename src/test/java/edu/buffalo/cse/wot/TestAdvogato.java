package edu.buffalo.cse.wot;

import java.util.ArrayList;
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
import edu.buffalo.cse.wot.neo4j.datastore.TrustOutput;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;
import edu.buffalo.cse.wot.neo4j.utils.ScoreUtils;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils.TRUST_DECAY_TYPE;

/**
 * 
 * @author varunjai
 *
 */
public class TestAdvogato {

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
    Pair<List<Long>, JsonNode> pair = DataUtils.loadAdvogatoGraph(dsm);
    uids = pair.getKey();
  }

  @org.junit.Test
  public void testShuffledAdvogato() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.75 shuffled");

    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.70f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());

      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledAdvogato2() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.5 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledAdvogato3() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.25 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledAdvogato4() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.1 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.1f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffleTarjan1() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.75 shuffled");

    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.70f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());

      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan2() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.5 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan3() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.25 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan4() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 2189;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Advogato: 0.1 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.1f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);
      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }
}
