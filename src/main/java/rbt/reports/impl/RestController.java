package rbt.reports.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Map;

/**
 * Обработка REST-запросов к модулю
 */
@Controller
@RequestMapping("/documents")
public class RestController {

  @Autowired
  private ReportManagerImpl manager;

  /**
   * Получение данных отчета по его идентификатору (для построения печатной формы)
   * @return
   */
  @RequestMapping(value = "/{docId}/{table}", method = RequestMethod.GET)
  public @ResponseBody Collection<Map<String, Object>> getTableData(@PathVariable String docId,
                                                                    @PathVariable String table) {
    return manager.getReportTableData(docId, table);
  }
}
