package rbt.reports.impl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Обработка REST-запросов к модулю
 */
@Controller
@RequestMapping("/documents")
public class RestController {
  @RequestMapping(value = "/ping", method = RequestMethod.GET)
  public String ping() {
    return "ping";
  }
}
