package edu.buffalo.cse.wot.neo4j.utils;

import java.util.List;

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
  
  /**
   * 
   * @param arr
   * @return
   */
  public static double   moleTrust(List<Double> arr) {
    double sum = 0;
    double prod = 0;
    
    for(int i = 0; i < arr.size(); i++) {
       sum += arr.get(i);
       prod *= arr.get(i);
    }
    return sum / prod;
  }

}
