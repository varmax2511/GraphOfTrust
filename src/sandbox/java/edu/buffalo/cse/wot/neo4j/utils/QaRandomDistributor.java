package edu.buffalo.cse.wot.neo4j.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  /**
   * Shuffles the input list and returns the split based on the ratio.
   *
   * @param list
   * @param ratio
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getShuffled(final List<Long> list,
      float ratio, long excludeId) {

    return getShuffledSizedSplit(list, ratio, list.size(), excludeId);
  }

  /**
   *
   * @param list
   * @param ratio
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getUnshuffledSplit(
      final List<Long> list, float ratio, int size, long excludeId) {

    // adjust size
    if (size >= list.size()) {
      size = list.size() - 1;
    }

    final List<Long> listCopy = new ArrayList<>(list);
    listCopy.remove(excludeId);
    
    final int n = (int) (ratio * size);
    return new Pair<Set<Long>, Set<Long>>(
        new HashSet<Long>(listCopy.subList(0, n)),
        new HashSet<Long>(listCopy.subList(n, size)));
  }

  /**
   *
   * @param list
   * @param ratio
   * @param size
   * @return
   */
  public static Pair<Set<Long>, Set<Long>> getShuffledSizedSplit(
      final List<Long> list, float ratio, int size, long excludeId) {

    // adjust size
    if (size >= list.size()) {
      size = list.size() - 1;
    }

    final List<Long> listCopy = new ArrayList<>(list);
    listCopy.remove(excludeId);
    Collections.shuffle(listCopy);
    final int n = (int) (ratio * size);
    return new Pair<Set<Long>, Set<Long>>(
        new HashSet<Long>(listCopy.subList(0, n)),
        new HashSet<Long>(listCopy.subList(n, size)));
  }

}
