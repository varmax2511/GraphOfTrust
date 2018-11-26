package edu.buffalo.cse.wot.neo4j;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.utils.DataUtils;

/**
 * 
 * @author varunjai
 *
 */
@SpringBootApplication
public class Main {

  private static Logger logger = LogManager.getLogger(Main.class);
  public static void main(String[] args) throws Exception {
    
    
    //JettyServer server = new JettyServer();
    //server.start();
    SpringApplication.run(Main.class, args);
    DataUtils.loadAdvogatoGraph(DataStoreManager.getInstance());
    
    /*UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    Edge edge1 = new Edge(node1, node2, 1.0f - 0.8f);
    node1.addEdge(edge1);

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));
    node2.addEdge(new Edge(node2, node5, 1.0f - 1.0f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));

    node4.addEdge(new Edge(node4, node5, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node3, 1.0f - 0.4f));

    DataStoreManager dsm = DataStoreManager.getInstance();
    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);


    // find the shortest path
    long minUid = dsm.getShortestPathUidFromSrc(AppConstants.LABEL_USER, "1");

    List<Long> uids = new ArrayList<Long>();
    uids.add(1l);
    uids.add(2l);
    uids.add(3l);
    uids.add(4l);
    uids.add(5l);
    
    final Pair<List<Long>, List<Long>> distribution = QaRandomDistributor
        .getRandom(uids, 0.75f);

    logger.info("Trust response: {}",
        distribution.getKey().indexOf(minUid) == -1 ? "YES" : "NO");
*/    
   // server.stop();
  }
}
