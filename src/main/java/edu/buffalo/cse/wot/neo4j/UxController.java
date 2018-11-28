package edu.buffalo.cse.wot.neo4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UxController {

  @RequestMapping(value = "/")
  public String index() {
    return "index";
  }
}
