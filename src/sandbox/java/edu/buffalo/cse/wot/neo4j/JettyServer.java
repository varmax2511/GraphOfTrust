package edu.buffalo.cse.wot.neo4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;

/**
 * 
 * @author varunjai
 *
 */
public class JettyServer {

  private static Logger logger = LogManager.getLogger(JettyServer.class);
  private Server server;

  /**
   * 
   * @throws Exception
   */
  public void start() throws Exception {
    final int maxThreads = 100;
    final int minThreads = 10;
    final int idleTimeout = 120;

    final QueuedThreadPool threadPool = new QueuedThreadPool(maxThreads,
        minThreads, idleTimeout);

    server = new Server(threadPool);
    final ServerConnector connector = new ServerConnector(server);
    connector.setPort(8090);
    server.setConnectors(new Connector[]{connector});

    logger.info("Starting Jetty server.....");
    server.start();
    logger.info("Jetty Server running on port 8090");
    logger.info("Starting Datastore...");
    DataStoreManager.getInstance();
  }

  /**
   *
   * @throws Exception
   */
  public void stop() throws Exception {
    DataStoreManager.getInstance().stop();
    server.stop();
  }
  
  /**
   * Clean the 
   */
  public void reset() {
    DataStoreManager.getInstance().reset();
  }
}
