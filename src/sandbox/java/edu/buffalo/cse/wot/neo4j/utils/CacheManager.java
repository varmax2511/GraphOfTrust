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

  private List<Long> uids = new LinkedList<>();
  private final AppConfiguration appConfiguration;
  
  /**
   * 
   */
  private CacheManager() {
    this.appConfiguration = AppConfiguration.getInstance();
    loadUids(Long.parseLong(appConfiguration.getProperty("config.user.size")));
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
  
  private void loadUids(long size) {
    for(long i = 1; i <= size; i++) {
      uids.add(i);
    }
  }
  
  public List<Long> getUids(){
    return this.uids;
  }
}
