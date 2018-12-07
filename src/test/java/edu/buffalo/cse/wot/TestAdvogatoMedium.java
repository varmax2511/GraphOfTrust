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

public class TestAdvogatoMedium {

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
    Pair<List<Long>, JsonNode> pair = DataUtils.loadAdvogatoMediumGraph(dsm);
    uids = pair.getKey();
  }

 
  @org.junit.Test
  public void testShuffleTarjan1() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.75 shuffled Log Trust");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan2() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.5 shuffled Log Trust");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan3() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.25 shuffled Log Trust");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjan4() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Dijkistra: 0.15 shuffled Log Trust");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.15f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffleTarjan5() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 1.0 shuffled Log Trust");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 1.0f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }
  
 
  @org.junit.Test
  public void testShuffleTarjanProb1() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.75 Cumulative shuffled");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.75f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb2() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.5 Cumulative shuffled");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.5f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb3() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.25 Cumulative shuffled");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.25f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());
      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @org.junit.Test
  public void testShuffledTarjanProb4() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 0.15 Cumulative shuffled");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 0.15f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());

      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }
  
  @org.junit.Test
  public void testShuffledTarjanProb5() throws InterruptedException {

    final List<Boolean> expected = new ArrayList<>();
    final List<Boolean> actual = new ArrayList<>();

    final long id = 12;
    List<TrustOutput> trustOutputs = new ArrayList<>();
    logger.info("Tarjan: 1.0 Cumulative shuffled");
    for (int i = 0; i < 30; i++) {
      actual.add(true);
      final Pair<Set<Long>, Set<Long>> distribution = QaRandomDistributor
          .getShuffled(uids, 1.0f, id, true);

      TrustOutput trustOutput = DataStoreManager.getInstance()
          .getResponseFrmSCC(AppConstants.LABEL_USER, String.valueOf(id),
              uids.size(), TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY,
              distribution);
      trustOutputs.add(trustOutput);

      expected.add(trustOutput.getResult());
      System.out.println(trustOutput.getConfidence());

      Thread.sleep(1000);
    } // for

    System.out.println(ScoreUtils.getRMSE(expected, actual));
  }

  @AfterClass
  public static void testDestroy() throws Exception {
    server.stop();
  }

}
