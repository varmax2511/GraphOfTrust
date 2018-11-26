package edu.buffalo.cse.wot.neo4j.utils;

import java.util.List;
import java.util.Set;

import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;

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
  public static double getRMSE(List<Boolean> expected, List<Boolean> actual) {

    if (expected == null || actual == null) {
      throw new IllegalArgumentException();
    }

    if (expected.size() != actual.size()) {
      throw new IllegalArgumentException(
          "FATAL: Number of actual and expected values don't match!");
    }

    double sum = 0;
    for (int i = 0; i < expected.size(); i++) {
      sum += Math.pow((expected.get(i) ? 1 : 0) - (actual.get(i) ? 1 : 0), 2);
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
  public static double getMAE(List<Boolean> expected, List<Boolean> actual) {

    if (expected == null || actual == null) {
      throw new IllegalArgumentException();
    }

    if (expected.size() != actual.size()) {
      throw new IllegalArgumentException(
          "FATAL: Number of actual and expected values don't match!");
    }

    double sum = 0;
    for (int i = 0; i < expected.size(); i++) {
      sum += Math.abs((expected.get(i) ? 1 : 0) - (actual.get(i) ? 1 : 0));
    } // for

    return (sum / expected.size());

  }

  /**
   * <pre>
   * Feedback
   * A user can submit feedback for the response provided to him/her.
   * This feedback can be positive or negative which means user likes the answer
   * or dislikes it.
   * A feedback will be processed only if it is relation to the actual answer.
   * This means that if a user gives positive feedback for an incorrect answer,
   * or negative feedback for a correct answer, feedback will be disregarded.
   * 
   * A positive feedback will increase the ratings of all those users who gave
   * the correct answer, they will be rewarded with a improved trust rating
   * on all incoming edges to them
   * 
   * A negative feedback will penalize all nodes giving an incorrect answer, it
   * will penalize all incoming edges of these nodes.
   * </pre>
   */
  public static void processFeedback(String labelName, boolean actualAnswer,
      boolean expectedAnswer, boolean feedback,
      Pair<Set<Long>, Set<Long>> distribution) {
    // invalid feedback
    if (actualAnswer == expectedAnswer) {
      if (feedback == false) {
        return;
      }
    } else {
      if (feedback) {
        return;
      }
    }

    // process the list of all node UID's which participated
    // if feedback is positive
    if (actualAnswer) {
      DataStoreManager.assignFeedback(labelName, feedback,
          distribution.getKey());
    } else {
      DataStoreManager.assignFeedback(labelName, feedback,
          distribution.getValue());
    }
  }
}
