package edu.buffalo.cse.wot.neo4j.utils;

import java.util.LinkedList;
import java.util.List;

import edu.buffalo.cse.wot.neo4j.config.AppConfiguration;

/**
 * 
 * @author varunjai
 *
 */
public class CacheManager {

  private final AppConfiguration appConfiguration;
  
  /**
   * 
   */
  private CacheManager() {
    this.appConfiguration = AppConfiguration.getInstance();
  }

  /**
   * 
   * @author varunjai
   *
   */
  private static class LazyHolder {
    private static final CacheManager INSTANCE = new CacheManager();
  }

  /**
   * 
   * @return
   */
  public static CacheManager getInstance() {
    return LazyHolder.INSTANCE;
  }
  
}
