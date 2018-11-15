package edu.buffalo.cse.wot.neo4j.datastore;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.TransactionTerminatedException;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.io.fs.FileUtils;

import edu.buffalo.cse.wot.neo4j.DataStore;
import edu.buffalo.cse.wot.neo4j.config.AppConfiguration;
import edu.buffalo.cse.wot.neo4j.config.AppConstants;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.GraphNode;
import edu.buffalo.cse.wot.neo4j.utils.GraphNodeUtils;

/**
 *
 * @author varunjai
 *
 */
class Neo4jStore implements DataStore {

  private final AppConfiguration appConfiguration;
  private GraphDatabaseService graphDb;
  private static File storeDir;
  private static Logger logger = LogManager.getLogger(Neo4jStore.class);
  /**
   *
   * @param configuration
   *          !null
   */
  private Neo4jStore() {
    this.appConfiguration = AppConfiguration.getInstance();
  }

  private static class LazyHolder {
    private static final Neo4jStore INSTANCE = new Neo4jStore();
  }

  public static Neo4jStore getInstance() {
    return LazyHolder.INSTANCE;
  }

  /**
   * @throws IOException
   *
   */
  private void createDb() throws IOException {

    storeDir = new File(
        appConfiguration.getProperty("config.database.directory"));

    // if directory not created or directory is a file
    if (!storeDir.exists() || !storeDir.isDirectory()) {
      FileUtils.deleteRecursively(storeDir);
    }

    // launch database service

    @SuppressWarnings("deprecation")
    GraphDatabaseSettings.BoltConnector bolt = GraphDatabaseSettings
        .boltConnector("0");
    graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(storeDir)
        .setConfig(bolt.type, "BOLT").setConfig(bolt.enabled, "true")
        .setConfig(bolt.address, "0.0.0.0:7687")
        .setConfig(bolt.encryption_level, "DISABLED") // dbms.connector.0.tls_level
        .setConfig("dbms.connector.http.address", "0.0.0.0:7474")
        .setConfig("dbms.connector.http.enabled", "true")
        .setConfig("dbms.connector.http.type", "HTTP").newGraphDatabase();

    /*
     * GraphDatabaseSettings.BoltConnector bolt =
     * GraphDatabaseSettings.boltConnector( "0" );
     * 
     * graphDb = new GraphDatabaseFactory() .newEmbeddedDatabaseBuilder(
     * storeDir ) .setConfig( bolt.type, "BOLT" ) .setConfig( bolt.enabled,
     * "true" ) .setConfig( bolt.address, "localhost:7687" )
     * .newGraphDatabase();
     */
    // graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(storeDir);
    // create schema and index definition.
    createSchema();

    // register shutdown hook
    registerShutdownHook(graphDb);
  }

  /**
   * Create the Schema for tables. The schema for now is fixed for two tables
   * QUESTION and USER. Index is created in {@link AppConstants#NODE_UID}
   */
  private void createSchema() {
    /*
     * Index definition for User label
     */
    try (Transaction tx = graphDb.beginTx()) {
      final Schema schema = graphDb.schema();
      schema.indexFor(Label.label(AppConstants.LABEL_USER))
          .on(AppConstants.NODE_UID).create();
      tx.success();
    }

    /*
     * Index definition for User label
     */
    try (Transaction tx = graphDb.beginTx()) {
      final Schema schema = graphDb.schema();
      schema.indexFor(Label.label(AppConstants.LABEL_QUESTION))
          .on(AppConstants.NODE_UID).create();
      tx.success();
    }

  }

  @Override
  public void startServer() {
    try {
      createDb();
      logger.info("Neo4j database instance started");
    } catch (Throwable t) {
      logger.error(t.getMessage());
      throw new IllegalArgumentException(
          "Neo4j: Database load configuration : " + t.getMessage());
    }
  }

  @Override
  public void stopServer() {
    graphDb.shutdown();
  }

  @Override
  public void save(GraphNode graphNode) {

    try (Transaction tx = graphDb.beginTx()) {
      final Label label = Label.label(GraphNodeUtils.getNodeLabel(graphNode));

      final Node node = getOrCreateNode(graphNode, label);

      /*
       * add relationship
       */
      for (final Edge edge : graphNode.getEdges()) {

        final Node endNode = getOrCreateNode(edge.getEndNode(), label);

        final Relationship relation = node.createRelationshipTo(endNode,
            RelationshipType.withName(AppConstants.RELATIONSHIP_TYPE_EDGE));
        relation.setProperty(AppConstants.RELATIONSHIP_EDGE_WEIGHT,
            edge.getWeight());

      } // for

      tx.success();
    } catch (TransactionTerminatedException ignored) {
      logger.error("Transaction terminated for node: " + graphNode);
    }

  }

  /**
   * Create a new {@link Node}.
   * 
   * @param graphNode
   * @param label
   * @return
   */
  private Node getOrCreateNode(GraphNode graphNode, final Label label) {

    Node node = graphDb.findNode(label, AppConstants.NODE_UID,
        graphNode.getPropertyByKey(AppConstants.NODE_UID));

    // found node
    if (node != null) {
      return node;
    }

    node = graphDb.createNode(label);
    for (Map.Entry<String, Object> entry : graphNode.getAllProperties()
        .entrySet()) {
      node.setProperty(entry.getKey(), entry.getValue());
    } // for
    return node;
  }

  /**
   * Register a shutdown hook,
   * 
   * @param graphDb
   */
  private static void registerShutdownHook(final GraphDatabaseService graphDb) {
    // Registers a shutdown hook for the Neo4j instance so that it
    // shuts down nicely when the VM exits (even if you "Ctrl-C" the
    // running application).
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        try {
          logger.info("Cleaning Neo4j instance files");
          FileUtils.deleteRecursively(storeDir);
        } catch (Throwable t) {
          // TODO Auto-generated catch block
          logger.warn("Database cleanup failed:" + t.getMessage());
        }
        graphDb.shutdown();
      }
    });
  }

  @Override
  public Node getNode(String labelName, String uid) {
    Validate.notBlank(labelName);
    Validate.notBlank(uid);

    return graphDb.findNode(Label.label(labelName), AppConstants.NODE_UID, uid);
  }

  public GraphDatabaseService getGraphDB() {
    return graphDb;
  }
}
