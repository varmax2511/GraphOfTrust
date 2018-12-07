package edu.buffalo.cse.wot;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    assertTrue(DataStoreManager.getInstance()
        .getShortestStrongestResponse(AppConstants.LABEL_USER,
            String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution)
        .getResult());
  }

  @org.junit.Test
  public void testFixedDijkstra2() {

    long id = 3;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.5f, uids.size(), id, true);

    // find the shortest path
    assertTrue(DataStoreManager.getInstance()
        .getShortestStrongestResponse(AppConstants.LABEL_USER,
            String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution)
        .getResult());
  }

  /**
   * 
   */
  @org.junit.Test
  public void testFixedDijkstra3() {

    long id = 2;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.25f, uids.size(), id, true);

    assertTrue(DataStoreManager.getInstance()
        .getShortestStrongestResponse(AppConstants.LABEL_USER,
            String.valueOf(id), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
            distribution)
        .getResult());
  }

  @org.junit.Test
  public void testFixedDijkstra4() {
    long id = 8;
    final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
        .getUnshuffledSplit(uids, 0.1f, uids.size(), id, true);

    assertFalse(DataStoreManager.getInstance()
        .getShortestStrongestResponse(AppConstants.LABEL_USER,
            String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution)
        .getResult());
  }

  @org.junit.Test
  public void testShuffledDijkstra1() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkista: 0.75 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

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
  public void testShuffledDijkstra2() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.5 shuffled");
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
  public void testShuffledDijkstra3() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.25 shuffled");
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
  public void testShuffledDijkstra4() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.1 shuffled");
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
  public void testShuffleTarjan1() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.75 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan2() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.5 shuffled");
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
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan3() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.25 shuffled");
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
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan4() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.15 shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.15f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledDijkstraProb1() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkista: 0.75 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledDijkstraProb2() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.5 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledDijkstraProb3() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.25 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledDijkstraProb4() {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.1 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffleTarjanProb1() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.75 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb2() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.5 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb3() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.25 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb4() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 3;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.15 Cumulative shuffled");
    for (int i = 0; i < 10; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.15f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      logger.info(trustOutput.getConfidence());
      
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  
  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }

}
