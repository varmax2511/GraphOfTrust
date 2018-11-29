package edu.buffalo.cse.wot.neo4j;

import java.util.ArrayList;
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

import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.datastore.TrustOutput;
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
    graphs.add(GRAPH_TYPE.SMALL_SIMPLE1.toString());
    graphs.add(GRAPH_TYPE.SMALL_DENSE1.toString());
    graphs.add(GRAPH_TYPE.ADVOGATO.toString());
    return graphs;
  }

  @RequestMapping(
      value = "/configure/{graphType}",
      method = RequestMethod.GET,
      produces = "application/json")
  public @ResponseBody Integer configure(@PathVariable String graphType) {

    logger.info("confguring.....");
    try {
      dsm.reset();
      uids.clear();
      uids = DataUtils.loadGraph(graphType, dsm);
      return uids.size();
    } catch (final Throwable t) {
      logger.error(t.getMessage());
    }
    return 0;
  }

  @RequestMapping(
      value = "/execute/{id}",
      method = RequestMethod.GET,
      produces = "application/json")
  public List<TrustOutput> execute(@PathVariable int id,
      @RequestParam(defaultValue = "0.75f") Float ratio,
      @RequestParam(defaultValue = "false") Boolean shuffled,
      @RequestParam(defaultValue = "5") Integer participants) {

    try {
      logger.info("request..");
      if (participants == null || participants > uids.size()) {
        participants = uids.size();
      }

      // algorithm, trust decay type, participants, fixed/shuffled
      if (!shuffled) {
        yayNnay = QaRandomDistributor.getUnshuffledSplit(uids, ratio,
            participants, id);
      } else {
        yayNnay = QaRandomDistributor.getShuffledSizedSplit(uids, ratio,
            participants, id);
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

      return trustOutputs;
    } catch (Throwable t) {
      logger.error(t.getMessage());
      t.printStackTrace();
    }

    return null;
  }

  @RequestMapping(
      value = "/feedback",
      method = RequestMethod.GET,
      produces = "application/json")
  public int assignFeedback(@RequestParam boolean generatedAnswer,
      @RequestParam boolean actualAnswer, @RequestParam boolean feedback) {
    logger.info("processing feedback");
    if (yayNnay == null) {
      return 0;
    }

    ScoreUtils.processFeedback(AppConstants.LABEL_USER, actualAnswer,
        generatedAnswer, feedback, yayNnay);
    yayNnay = null;
    return 1;
  }

}
