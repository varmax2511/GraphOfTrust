package edu.buffalo.cse.wot.neo4j.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.buffalo.cse.wot.neo4j.Pair;

/**
 *
 * @author varunjai
 *
 */
public class QaRandomDistributor {

  /**
   *
   * @param list
   * @param ratio
   * @return
   */
  public static Pair<List<Long>, List<Long>> getRandom(List<Long> list,
      float ratio) {

    final List<Long> listCopy = new ArrayList<>(list);
    Collections.shuffle(listCopy);
    final int n = (int) (ratio * list.size());
    return new Pair<List<Long>, List<Long>>(listCopy.subList(0, n),
        listCopy.subList(n, listCopy.size()));
  }
}
