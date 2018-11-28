package edu.buffalo.cse.wot.neo4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @author varunjai
 *
 */
@SpringBootApplication
public class Main {

  private static Logger logger = LogManager.getLogger(Main.class);
  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }
}
