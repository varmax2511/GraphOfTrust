package edu.buffalo.cse.wot.neo4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.datastore.TrustOutput;
import edu.buffalo.cse.wot.neo4j.model.Question;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils.GRAPH_TYPE;
import edu.buffalo.cse.wot.neo4j.utils.QaRandomDistributor;
import edu.buffalo.cse.wot.neo4j.utils.ScoreUtils;
import edu.buffalo.cse.wot.neo4j.utils.TrustDecayUtils.TRUST_DECAY_TYPE;

/**
 * 
 * @author varunjai
 *
 */
@RestController
@RequestMapping("/graph")
public class ApiController {

  private static DataStoreManager dsm;
  private List<Long> uids = new ArrayList<>();
  private Pair<Set<Long>, Set<Long>> yayNnay;
  private static Logger logger = LogManager.getLogger(ApiController.class);

  static {
    dsm = DataStoreManager.getInstance();
  }

  @RequestMapping(
      value = "/graphs",
      method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody List<String> getAvailableGraphs() {
    List<String> graphs = new ArrayList<>();
    graphs.add(GRAPH_TYPE.SMALL_SIMPLE.toString());
    graphs.add(GRAPH_TYPE.SMALL_BRIDGE.toString());
    graphs.add(GRAPH_TYPE.SMALL_DENSE.toString());
    graphs.add(GRAPH_TYPE.ADVOGATO_MEDIUM.toString());
    return graphs;
  }

  @RequestMapping(
      value = "/configure/{graphType}",
      method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody JsonNode configure(@PathVariable String graphType) {

    logger.info("confguring.....");
    try {
      dsm.reset();
      uids.clear();
      Pair<List<Long>, JsonNode> output = DataUtils.loadGraph(graphType, dsm);
      uids = output.getKey();
      return output.getValue();
      // return uids.size();
    } catch (final Throwable t) {
      logger.error(t.getMessage());
    }
    return null;
  }

  @RequestMapping(
      value = "/questions",
      method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody List<Question> getAllQuestions() {

    List<Question> questions = new ArrayList<>();
    try {
      questions = DataUtils.loadQA();
    } catch (IOException e) {
      logger.error(e.getMessage());
      e.printStackTrace();
    }

    return questions;
  }

  @RequestMapping(
      value = "/execute/{id}",
      method = RequestMethod.GET,
      produces = "application/json")
  public List<TrustOutput> execute(@PathVariable int id,
      @RequestParam(defaultValue = "0.75f") Float ratio,
      @RequestParam(defaultValue = "false") Boolean shuffled,
      @RequestParam(defaultValue = "5") Integer participants,
      @RequestParam Boolean answer) {

    try {
      logger.info("request..");
      if (participants == null || participants > uids.size()) {
        participants = uids.size();
      }

      // algorithm, trust decay type, participants, fixed/shuffled
      if (!shuffled) {
        yayNnay = QaRandomDistributor.getUnshuffledSplit(uids, ratio,
            participants, id, answer);
      } else {
        yayNnay = QaRandomDistributor.getShuffledSizedSplit(uids, ratio,
            participants, id, answer);
      }

      final List<TrustOutput> trustOutputs = new ArrayList<>();

      trustOutputs.add(DataStoreManager.getInstance().getResponseFrmSCC(
          AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
          TRUST_DECAY_TYPE.LOG_TRUST_DECAY, yayNnay));

      trustOutputs.add(DataStoreManager.getInstance().getResponseFrmSCC(
          AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
          TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, yayNnay));

      trustOutputs.add(DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, yayNnay));

      trustOutputs
          .add(DataStoreManager.getInstance().getShortestStrongestResponse(
              AppConstants.LABEL_USER, String.valueOf(id),
              TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, yayNnay));

      // input actual answer
      for (TrustOutput trustOutput : trustOutputs) {
        trustOutput.setAnswer(answer);
      }

      return trustOutputs;
    } catch (Throwable t) {
      logger.error(t.getMessage());
      t.printStackTrace();
    }

    return null;
  }

  @RequestMapping(
      value = "/executeIds/{id}",
      method = RequestMethod.GET,
      produces = "application/json")
  public List<TrustOutput> executeWthIds(@PathVariable int id,
      @RequestParam String yesIds, @RequestParam String noIds,
      @RequestParam Boolean answer) {

    try {
      logger.info("request..");

      final Set<Long> yes = new HashSet<>();
      final Set<Long> no = new HashSet<>();
      for (String yesId : yesIds.split(",")) {
        yes.add(Long.parseLong(yesId));
      }

      for (String noId : noIds.split(",")) {
        no.add(Long.parseLong(noId));
      }

      yayNnay = new Pair<Set<Long>, Set<Long>>(yes, no);

      final List<TrustOutput> trustOutputs = new ArrayList<>();

      trustOutputs.add(DataStoreManager.getInstance().getResponseFrmSCC(
          AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
          TRUST_DECAY_TYPE.LOG_TRUST_DECAY, yayNnay));

      trustOutputs.add(DataStoreManager.getInstance().getResponseFrmSCC(
          AppConstants.LABEL_USER, String.valueOf(id), uids.size(),
          TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, yayNnay));

      trustOutputs.add(DataStoreManager.getInstance()
          .getShortestStrongestResponse(AppConstants.LABEL_USER,
              String.valueOf(id), TRUST_DECAY_TYPE.LOG_TRUST_DECAY, yayNnay));

      trustOutputs
          .add(DataStoreManager.getInstance().getShortestStrongestResponse(
              AppConstants.LABEL_USER, String.valueOf(id),
              TRUST_DECAY_TYPE.CUMULATIVE_TRUST_DECAY, yayNnay));

      // input actual answer
      for (TrustOutput trustOutput : trustOutputs) {
        trustOutput.setAnswer(answer);
      }

      return trustOutputs;
    } catch (Throwable t) {
      logger.error(t.getMessage());
      t.printStackTrace();
    }

    return null;
  }

  @RequestMapping(
      value = "/feedback/{feedback}",
      method = RequestMethod.GET,
      produces = "application/json")
  public int assignFeedback(@PathVariable boolean feedback,
      @RequestParam(name = "answer") boolean generatedAnswer) {
    logger.info("processing feedback");
    if (yayNnay == null) {
      return 0;
    }

    ScoreUtils.processFeedback(AppConstants.LABEL_USER, generatedAnswer,
        feedback, yayNnay);
    yayNnay = null;
    return 1;
  }

}
