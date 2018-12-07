package edu.buffalo.cse.wot.neo4j.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;

/**
 * The purpose of this class to process a list of {@link AppConstants#NODE_UID}
 * Based on the passed size and ratio, from the input list generate a list of
 * given size split in two halves based on the ratio.
 *
 * @author varunjai
 *
 */
public class QaRandomDistributor {

  private static Logger logger = LogManager
      .getLogger(QaRandomDistributor.class);
  /**
   * Shuffles the input list and returns the split based on the ratio.
   *
   * @param list
   * @param ratio
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getShuffled(final List<Long> list,
      float ratio, long excludeId, boolean answer) {

    return getShuffledSizedSplit(list, ratio, list.size(), excludeId, answer);
  }

  /**
   *
   * @param list
   * @param ratio
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getUnshuffledSplit(
      final List<Long> list, float ratio, int size, long excludeId,
      boolean answer) {

    if (list == null || list.size() == 0 || size < 1) {
      logger.error("FATAL: Invalid list or input size");
      throw new IllegalArgumentException("Invalid list or input size");
    }

    // adjust size
    if (size >= list.size()) {
      size = list.size() - 1;
    }

    final List<Long> listCopy = new ArrayList<>(list);
    listCopy.remove(excludeId);

    final int n = (int) (ratio * size);

    if (answer) {
      return new Pair<Set<Long>, Set<Long>>(
          new HashSet<Long>(listCopy.subList(0, n)),
          new HashSet<Long>(listCopy.subList(n, size)));
    }

    return new Pair<Set<Long>, Set<Long>>(
        new HashSet<Long>(listCopy.subList(n, size)),
        new HashSet<Long>(listCopy.subList(0, n)));
  }

  /**
   *
   * @param list
   * @param ratio
   * @param size
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getShuffledSizedSplit(
      final List<Long> list, float ratio, int size, long excludeId,
      boolean answer) {

    // adjust size
    if (size >= list.size()) {
      size = list.size() - 1;
    }

    final List<Long> listCopy = new ArrayList<>(list);
    listCopy.remove(excludeId);
    Collections.shuffle(listCopy);
    final int n = (int) (ratio * size);

    if (answer) {
      return new Pair<Set<Long>, Set<Long>>(
          new HashSet<Long>(listCopy.subList(0, n)),
          new HashSet<Long>(listCopy.subList(n, size)));
    }

    return new Pair<Set<Long>, Set<Long>>(
        new HashSet<Long>(listCopy.subList(n, size)),
        new HashSet<Long>(listCopy.subList(0, n)));
  }

}
