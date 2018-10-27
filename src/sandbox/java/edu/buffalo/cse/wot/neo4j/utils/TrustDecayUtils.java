package edu.buffalo.cse.wot.neo4j.utils;

public class TrustDecayUtils {

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

}
