package edu.buffalo.cse.wot.neo4j.datastore;

import java.util.Set;

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
  private boolean answer;
  private Set<Long> yesIds;
  private Set<Long> noIds;

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
  public Set<Long> getYesIds() {
    return yesIds;
  }
  public void setYesIds(Set<Long> yesIds) {
    this.yesIds = yesIds;
  }
  public Set<Long> getNoIds() {
    return noIds;
  }
  public void setNoIds(Set<Long> noIds) {
    this.noIds = noIds;
  }
  public boolean isAnswer() {
    return answer;
  }
  public void setAnswer(boolean answer) {
    this.answer = answer;
  }

  
}
