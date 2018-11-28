package edu.buffalo.cse.wot.neo4j.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * 
 * @author varunjai
 *
 */
public class AppConfiguration {

  private final Properties properties;
  private static Logger logger = LogManager.getLogger(AppConfiguration.class);

  /**
   * 
   */
  private AppConfiguration() {
    properties = new Properties();
    try {
      properties.load(new FileInputStream(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\java\\config.properties"));
    } catch (Throwable t) {
      logger.error(t.getMessage());
      throw new IllegalArgumentException(
          "Invalid properties file: " + t.getMessage());
    }
  }

  private static class LazyHolder {
    private static final AppConfiguration INSTANCE = new AppConfiguration();
  }

  public static AppConfiguration getInstance() {
    return LazyHolder.INSTANCE;
  }

  /**
   * 
   * @param propertyName
   *          !blank
   * @return
   */
  public String getProperty(String propertyName) {
    // empty check
    if (StringUtils.isBlank(propertyName)) {
      return null;
    }
    return properties.getProperty(propertyName);
  }

  /**
   * 
   * @param propertyName
   * @param propertyValue
   */
  public void setProperty(String propertyName, String propertyValue) {
    // empty check
    if (StringUtils.isBlank(propertyName)
        || StringUtils.isBlank(propertyValue)) {
      return;
    }
    properties.setProperty(propertyName, propertyValue);
  }
}
