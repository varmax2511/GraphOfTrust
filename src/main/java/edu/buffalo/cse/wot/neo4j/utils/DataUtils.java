package edu.buffalo.cse.wot.neo4j.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import edu.buffalo.cse.wot.neo4j.Pair;
import edu.buffalo.cse.wot.neo4j.datastore.DataStoreManager;
import edu.buffalo.cse.wot.neo4j.model.Edge;
import edu.buffalo.cse.wot.neo4j.model.GraphNode;
import edu.buffalo.cse.wot.neo4j.model.Question;
import edu.buffalo.cse.wot.neo4j.model.UserNode;

/**
 * Load the datasets
 *
 * @author varunjai
 *
 */
public class DataUtils {

  public enum GRAPH_TYPE {
    SMALL_DENSE {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "SMALL_DENSE";
      }
    },
    SMALL_BRIDGE {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "SMALL_BRIDGE";
      }
    },
    SMALL_SIMPLE {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "SMALL_SIMPLE";
      }
    },
    ADVOGATO {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "ADVOGATO";
      }
    },
    ADVOGATO_MEDIUM {
      @Override
      public String toString() {
        // TODO Auto-generated method stub
        return "ADVOGATO_MEDIUM";
      }
    }

  }

  /**
   *
   * @param graphType
   * @param dsm
   * @return
   * @throws IOException
   */
  public static Pair<List<Long>, JsonNode> loadGraph(String graphType,
      DataStoreManager dsm) throws IOException {
    if (GRAPH_TYPE.ADVOGATO.toString().equalsIgnoreCase(graphType)) {
      return loadAdvogatoGraph(dsm);
    } else if (GRAPH_TYPE.SMALL_DENSE.toString().equalsIgnoreCase(graphType)) {
      return loadSmallDenseGraph(dsm);
    } else if (GRAPH_TYPE.SMALL_BRIDGE.toString().equalsIgnoreCase(graphType)) {
      return loadSmallBridgeGraph(dsm);
    }else if (GRAPH_TYPE.ADVOGATO_MEDIUM.toString().equalsIgnoreCase(graphType)) {
      return loadAdvogatoMediumGraph(dsm);
    }
    return loadSmallGraph(dsm);
  }

  private static Logger logger = LogManager.getLogger(DataUtils.class);

  public static Pair<List<Long>, JsonNode> loadAdvogatoMediumGraph(
      DataStoreManager dsm) throws IOException {
    // validate
    if (dsm == null) {
      return null;
    }

    final Map<Long, UserNode> id2UserNodes = new HashMap<>();
    BufferedReader userNameReader = null;
    BufferedReader edgeReader = null;
    File userfile = null;
    File edgeFile = null;

    try {
      /*
       * Read user names
       */
      userfile = new File(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\resources\\advogato\\adogato.medium.name");
      userNameReader = new BufferedReader(new FileReader(userfile));
      String name = StringUtils.EMPTY;
      long cnt = 0l;
      // read and create node
      while ((name = userNameReader.readLine()) != null) {
        final UserNode node = new UserNode();
        node.setProperty("uid", String.valueOf(++cnt));
        node.setProperty("name", name);
        id2UserNodes.put(cnt, node);

      } // while

      /*
       * Read relationships
       */
      edgeFile = new File(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\resources\\advogato\\out.medium.advogato");
      edgeReader = new BufferedReader(new FileReader(edgeFile));
      String edge = "";

      // iterate relationships
      while ((edge = edgeReader.readLine()) != null) {
        final String[] arr = edge.split(" ");
        // skip
        if (arr == null || arr.length != 3) {
          continue;
        }

        final long src = Long.parseLong(arr[0]);
        final long end = Long.parseLong(arr[1]);

        // no self loops
        if (src == end) {
          continue;
        }

        final float weight = 1 - Float.parseFloat(arr[2]);

        final UserNode startNode = id2UserNodes.get(src);
        final UserNode endNode = id2UserNodes.get(end);

        // if unable to find source or destination nodes
        if (startNode == null || endNode == null) {
          logger.warn(
              "No matching node found for relationship source: {} -> end: {}",
              src, end);
          continue;
        }

        startNode.addEdge(
            new Edge(id2UserNodes.get(src), id2UserNodes.get(end), weight));

        id2UserNodes.put(src, startNode);
      } // while

      // iterate the map and add to db
      for (final Map.Entry<Long, UserNode> entry : id2UserNodes.entrySet()) {
        dsm.addNode(entry.getValue());
      } // for

      return new Pair<>(new ArrayList<Long>(id2UserNodes.keySet()),
          JsonNodeUtils
              .getJsonGraph(new ArrayList<GraphNode>(id2UserNodes.values())));
    } finally {

      /*
       * cleanup
       */
      if (userNameReader != null) {
        userNameReader.close();
      }

      if (edgeReader != null) {
        edgeReader.close();
      }

      userfile = null;
      edgeFile = null;
    }

  }

  /**
   * Load the Advogato dataset in Datastore
   *
   * @param dsm
   *          !null
   * @return number of nodes
   * @throws IOException
   */
  public static Pair<List<Long>, JsonNode> loadAdvogatoGraph(
      DataStoreManager dsm) throws IOException {
    // validate
    if (dsm == null) {
      return null;
    }

    final Map<Long, UserNode> id2UserNodes = new HashMap<>();
    BufferedReader userNameReader = null;
    BufferedReader edgeReader = null;
    File userfile = null;
    File edgeFile = null;

    try {
      /*
       * Read user names
       */
      userfile = new File(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\resources\\advogato\\ent.advogato.user.name");
      userNameReader = new BufferedReader(new FileReader(userfile));
      String name = StringUtils.EMPTY;
      long cnt = 0l;
      // read and create node
      while ((name = userNameReader.readLine()) != null) {
        final UserNode node = new UserNode();
        node.setProperty("uid", String.valueOf(++cnt));
        node.setProperty("name", name);
        id2UserNodes.put(++cnt, node);

      } // while

      /*
       * Read relationships
       */
      edgeFile = new File(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\resources\\advogato\\out.advogato");
      edgeReader = new BufferedReader(new FileReader(edgeFile));
      String edge = "";

      // iterate relationships
      while ((edge = edgeReader.readLine()) != null) {
        final String[] arr = edge.split(" ");
        // skip
        if (arr == null || arr.length != 3) {
          continue;
        }

        final long src = Long.parseLong(arr[0]);
        final long end = Long.parseLong(arr[1]);

        // no self loops
        if (src == end) {
          continue;
        }

        final float weight = 1 - Float.parseFloat(arr[2]);

        final UserNode startNode = id2UserNodes.get(src);
        final UserNode endNode = id2UserNodes.get(end);

        // if unable to find source or destination nodes
        if (startNode == null || endNode == null) {
          logger.warn(
              "No matching node found for relationship source: {} -> end: {}",
              src, end);
          continue;
        }

        startNode.addEdge(
            new Edge(id2UserNodes.get(src), id2UserNodes.get(end), weight));

        id2UserNodes.put(src, startNode);
      } // while

      // iterate the map and add to db
      for (final Map.Entry<Long, UserNode> entry : id2UserNodes.entrySet()) {
        dsm.addNode(entry.getValue());
      } // for

      return new Pair<>(new ArrayList<Long>(id2UserNodes.keySet()),
          JsonNodeUtils
              .getJsonGraph(new ArrayList<GraphNode>(id2UserNodes.values())));
    } finally {

      /*
       * cleanup
       */
      if (userNameReader != null) {
        userNameReader.close();
      }

      if (edgeReader != null) {
        edgeReader.close();
      }

      userfile = null;
      edgeFile = null;
    }
  }

  /**
   *
   * @return
   */
  public static Pair<List<Long>, JsonNode> loadSmallGraph(
      DataStoreManager dsm) {

    final List<GraphNode> graphNodes = new ArrayList<>();
    final UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    final UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    final UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    final UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    final UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    final Edge edge1 = new Edge(node1, node2, 1.0f - 0.8f);
    node1.addEdge(edge1);

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));
    node2.addEdge(new Edge(node2, node5, 1.0f - 1.0f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));

    node4.addEdge(new Edge(node4, node5, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node3, 1.0f - 0.4f));

    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);

    graphNodes.add(node1);
    graphNodes.add(node2);
    graphNodes.add(node3);
    graphNodes.add(node4);
    graphNodes.add(node5);

    final List<Long> uids = new ArrayList<>();
    uids.add(1l);
    uids.add(2l);
    uids.add(3l);
    uids.add(4l);
    uids.add(5l);
    return new Pair<List<Long>, JsonNode>(uids,
        JsonNodeUtils.getJsonGraph(graphNodes));
  }

  /**
   * Load a small dataset with back edges
   *
   * @return
   */
  public static Pair<List<Long>, JsonNode> loadSmallDenseGraph(
      DataStoreManager dsm) {
    final UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    final UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    final UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    final UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    final UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    final UserNode node6 = new UserNode();
    node6.setProperty("uid", "6");
    node6.setProperty("name", "F");

    final UserNode node7 = new UserNode();
    node7.setProperty("uid", "7");
    node7.setProperty("name", "G");

    final UserNode node8 = new UserNode();
    node8.setProperty("uid", "8");
    node8.setProperty("name", "H");

    final UserNode node9 = new UserNode();
    node9.setProperty("uid", "9");
    node9.setProperty("name", "I");

    final UserNode node10 = new UserNode();
    node10.setProperty("uid", "10");
    node10.setProperty("name", "J");

    node1.addEdge(new Edge(node1, node2, 1.0f - 0.8f));
    node1.addEdge(new Edge(node1, node3, 1.0f - 0.6f));

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));
    node3.addEdge(new Edge(node3, node5, 1.0f - 0.3f));
    node3.addEdge(new Edge(node3, node7, 1.0f - 0.4f));

    node4.addEdge(new Edge(node4, node1, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node6, 1.0f - 0.4f));

    node6.addEdge(new Edge(node6, node8, 1.0f - 0.3f));
    node6.addEdge(new Edge(node6, node10, 1.0f - 0.8f));

    node7.addEdge(new Edge(node7, node5, 1.0f - 0.7f));
    node7.addEdge(new Edge(node7, node6, 1.0f - 0.4f));

    node9.addEdge(new Edge(node9, node6, 1.0f - 0.1f));
    node9.addEdge(new Edge(node9, node8, 1.0f - 0.6f));

    node10.addEdge(new Edge(node10, node8, 1.0f - 0.4f));

    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);
    dsm.addNode(node6);
    dsm.addNode(node7);
    dsm.addNode(node8);
    dsm.addNode(node9);
    dsm.addNode(node10);

    final List<Long> uids = new ArrayList<>();
    uids.add(1l);
    uids.add(2l);
    uids.add(3l);
    uids.add(4l);
    uids.add(5l);
    uids.add(6l);
    uids.add(7l);
    uids.add(8l);
    uids.add(9l);
    uids.add(10l);

    final List<GraphNode> graphNodes = new ArrayList<>();
    graphNodes.add(node1);
    graphNodes.add(node2);
    graphNodes.add(node3);
    graphNodes.add(node4);
    graphNodes.add(node5);
    graphNodes.add(node6);
    graphNodes.add(node7);
    graphNodes.add(node8);
    graphNodes.add(node9);
    graphNodes.add(node10);

    return new Pair<List<Long>, JsonNode>(uids,
        JsonNodeUtils.getJsonGraph(graphNodes));
  }

  /**
   * Load a small dataset with back edges
   *
   * @return
   */
  public static Pair<List<Long>, JsonNode> loadSmallBridgeGraph(
      DataStoreManager dsm) {
    final UserNode node1 = new UserNode();
    node1.setProperty("uid", "1");
    node1.setProperty("name", "A");

    final UserNode node2 = new UserNode();
    node2.setProperty("uid", "2");
    node2.setProperty("name", "B");

    final UserNode node3 = new UserNode();
    node3.setProperty("uid", "3");
    node3.setProperty("name", "C");

    final UserNode node4 = new UserNode();
    node4.setProperty("uid", "4");
    node4.setProperty("name", "D");

    final UserNode node5 = new UserNode();
    node5.setProperty("uid", "5");
    node5.setProperty("name", "E");

    final UserNode node6 = new UserNode();
    node6.setProperty("uid", "6");
    node6.setProperty("name", "F");

    final UserNode node7 = new UserNode();
    node7.setProperty("uid", "7");
    node7.setProperty("name", "G");

    final UserNode node8 = new UserNode();
    node8.setProperty("uid", "8");
    node8.setProperty("name", "H");

    final UserNode node9 = new UserNode();
    node9.setProperty("uid", "9");
    node9.setProperty("name", "I");

    node1.addEdge(new Edge(node1, node2, 1.0f - 0.8f));
    node1.addEdge(new Edge(node1, node3, 1.0f - 0.6f));

    node2.addEdge(new Edge(node2, node3, 1.0f - 0.2f));

    node3.addEdge(new Edge(node3, node4, 1.0f - 0.5f));

    node4.addEdge(new Edge(node4, node1, 1.0f - 0.7f));

    node5.addEdge(new Edge(node5, node2, 1.0f - 0.7f));

    node6.addEdge(new Edge(node6, node5, 1.0f - 0.3f));
    node6.addEdge(new Edge(node6, node9, 1.0f - 0.7f));

    node7.addEdge(new Edge(node7, node6, 1.0f - 0.7f));
    node7.addEdge(new Edge(node7, node9, 1.0f - 0.4f));

    node8.addEdge(new Edge(node8, node7, 1.0f - 0.1f));
    node8.addEdge(new Edge(node8, node9, 1.0f - 0.6f));

    dsm.addNode(node1);
    dsm.addNode(node2);
    dsm.addNode(node3);
    dsm.addNode(node4);
    dsm.addNode(node5);
    dsm.addNode(node6);
    dsm.addNode(node7);
    dsm.addNode(node8);
    dsm.addNode(node9);

    final List<Long> uids = new ArrayList<>();
    uids.add(1l);
    uids.add(2l);
    uids.add(3l);
    uids.add(4l);
    uids.add(5l);
    uids.add(6l);
    uids.add(7l);
    uids.add(8l);
    uids.add(9l);

    final List<GraphNode> graphNodes = new ArrayList<>();
    graphNodes.add(node1);
    graphNodes.add(node2);
    graphNodes.add(node3);
    graphNodes.add(node4);
    graphNodes.add(node5);
    graphNodes.add(node6);
    graphNodes.add(node7);
    graphNodes.add(node8);
    graphNodes.add(node9);

    return new Pair<List<Long>, JsonNode>(uids,
        JsonNodeUtils.getJsonGraph(graphNodes));
  }

  /**
   * 
   * @return
   * @throws IOException
   */
  public static List<Question> loadQA() throws IOException {

    File qaFile = null;
    BufferedReader reader = null;
    final List<Question> questions = new ArrayList<>();

    try {
      qaFile = new File(
          "C:\\Users\\rathj\\git\\GraphOfTrust\\src\\main\\resources\\QA.txt");

      reader = new BufferedReader(new FileReader(qaFile));
      String text = StringUtils.EMPTY;

      while ((text = reader.readLine()) != null) {
        final String[] arr = text.split("\\|");
        final Question question = new Question();
        question.setQuestion(arr[0]);
        question.setAnswer(arr[1].equals("True") ? true : false);
        questions.add(question);
      } // while

      return questions;
    } finally {
      if (reader != null) {
        reader.close();
      }
    }

  }

}