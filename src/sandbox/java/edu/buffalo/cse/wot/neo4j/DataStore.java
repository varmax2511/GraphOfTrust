package edu.buffalo.cse.wot.neo4j;

import edu.buffalo.cse.wot.neo4j.model.GraphNode;

public interface DataStore {

  public void startServer();

  public void stopServer();

  public void save(GraphNode graphNode);
}
