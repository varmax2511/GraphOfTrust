package edu.buffalo.cse.wot.neo4j.model;

public class Question {

  private String question;
  private boolean answer;
  public String getQuestion() {
    return question;
  }
  public void setQuestion(String question) {
    this.question = question;
  }
  public boolean isAnswer() {
    return answer;
  }
  public void setAnswer(boolean answer) {
    this.answer = answer;
  }
  
}
