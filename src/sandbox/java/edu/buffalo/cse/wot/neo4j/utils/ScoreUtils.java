package edu.buffalo.cse.wot.neo4j.utils;

import java.util.List;

/**
 * 
 * @author varunjai
 *
 */
public class ScoreUtils {

  /**
   * Get Root Mean Squared Error
   * 
   * @param expected
   * @param actual
   * @return
   */
  public static double getRMSE(List<Float> expected, List<Float> actual) {

    if (expected == null || actual == null) {
      throw new IllegalArgumentException();
    }

    if (expected.size() != actual.size()) {
      throw new IllegalArgumentException(
          "FATAL: Number of actual and expected values don't match!");
    }

    double sum = 0;
    for (int i = 0; i < expected.size(); i++) {
      sum += Math.pow(expected.get(i) - actual.get(i), 2);
    } // for

    return Math.sqrt(sum / expected.size());

  }

  /**
   * Get the Mean absolute Error
   * 
   * @param expected
   * @param actual
   * @return
   */
  public static double getMAE(List<Float> expected, List<Float> actual) {

    if (expected == null || actual == null) {
      throw new IllegalArgumentException();
    }

    if (expected.size() != actual.size()) {
      throw new IllegalArgumentException(
          "FATAL: Number of actual and expected values don't match!");
    }

    double sum = 0;
    for (int i = 0; i < expected.size(); i++) {
      sum += Math.abs(expected.get(i) - actual.get(i));
    } // for

    return (sum / expected.size());

  }

}
