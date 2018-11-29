package edu.buffalo.cse.wot.neo4j.datastore;

/**
 * 
 * @author varunjai
 *
 */
public class TrustOutput {

  private boolean result;
  private double confidence;
  private String heuristic;
  private String trustDecayType;

  public boolean getResult() {
    return result;
  }
  public double getConfidence() {
    return confidence;
  }
  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }
  public String getHeuristic() {
    return heuristic;
  }
  public void setHeuristic(String heuristic) {
    this.heuristic = heuristic;
  }
  public void setResult(boolean result) {
    this.result = result;
  }
  public String getTrustDecayType() {
    return trustDecayType;
  }
  public void setTrustDecayType(String trustDecayType) {
    this.trustDecayType = trustDecayType;
  }

}
