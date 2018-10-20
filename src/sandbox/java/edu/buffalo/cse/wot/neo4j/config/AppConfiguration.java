package edu.buffalo.cse.wot.neo4j.config;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfiguration {

  private final Properties properties;
  private static Logger logger = LoggerFactory.getLogger(AppConfiguration.class);

  /**
   * 
   */
  public AppConfiguration() {
    properties = new Properties();
    try {
      properties.load(new FileInputStream("C:\\Users\\rathj\\git\\GraphOfTrust\\src\\sandbox\\java\\config.properties"));
    } catch (Throwable t) {
      logger.error(t.getMessage());
      throw new IllegalArgumentException(
          "Invalid properties file: " + t.getMessage());
    }
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
