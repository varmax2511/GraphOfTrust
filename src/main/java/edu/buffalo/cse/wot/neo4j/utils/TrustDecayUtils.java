package edu.buffalo.cse.wot.neo4j.utils;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author varunjai
 *
 */
public class TrustDecayUtils {

  public enum TRUST_DECAY_TYPE {
    LOG_TRUST_DECAY {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "Logarithmic Trust Decay";
      }
    },
    CUMULATIVE_TRUST_DECAY {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "Probabilistic Trust Decay";
      }
    }
  }
  private static Logger logger = LogManager.getLogger(TrustDecayUtils.class);
  /**
   * 
   * @param hops
   * @return
   */
  public static double logarithmicTrustDecay(int hops) {
    if (hops == 0) {
      return 0;
    }
    return Math.log(hops);
  }

  /**
   * sqrt(hops * product(edgeWeights))/sum(edgeWeights)
   * 
   * @param arr
   * @return
   */
  public static double cumulativeTrust(List<Float> edgeWeights, int hops) {
    float sum = 0;
    float prod = 0;

    for (int i = 0; i < edgeWeights.size(); i++) {
      sum += edgeWeights.get(i);
      prod *= edgeWeights.get(i);
    } // for
    return cumulativeTrust2(sum, prod, hops);
  }

  /**
   * 
   * @param edgeWeightSum
   * @param edgeWeightPrd
   * @param hops
   * @return
   */
  public static double cumulativeTrust2(float edgeWeightSum,
      float edgeWeightPrd, int hops) {
    try {
      return Math.sqrt((hops * edgeWeightPrd) / edgeWeightSum);
    } catch (Throwable t) {
      logger.error(t.getMessage());
    }

    return 0;
  }

}
