package rbt.reports;

import com.google.gson.Gson;
import rbt.reports.entities.*;

import java.util.*;

/**
 * Подсистема генерации ОПК.5.3.2
 */
public class ReportsGenerator {

  private String dbName = "db";

  public ReportsGenerator() {
  }

  public ReportsGenerator(String dbName) {
    this.dbName = dbName;
  }

  /**
   * Генерация набора команд для создания начального (пустого) содержания коллекции
   * @param docId идентификатор документа (совпадает с именем коллекции в БД)
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return команды по заполнению коллекции
   */
  public List<String> initialCollection(String docId, ReportDescriptor descriptor) {
    final String collectionPath = dbName + "." + docId + ".insert(";
    Gson gson = new Gson();

    Set<Map<String, Object>> emptyList = generateEmptyList(descriptor);
    List<String> result = new ArrayList<String>(emptyList.size());
    for (Map<String, Object> entry : emptyList)
      result.add(collectionPath + gson.toJson(entry) + ")");
    return result;
  }

  /**
   * Генерация структуры "пустого" json-объекта на основе описания столбцов отчета
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return описание требуемого json-объекта
   */
  private Map<String, Number> generateEmptyValue(ReportDescriptor descriptor) {
    Map<String, Number> result = new HashMap<String, Number>();
    if (descriptor.getColumns() != null)
      for (ReportColumn column : descriptor.getColumns())
        result.put(column.getId(), 0);
    return result;
  }

  /**
   * Генерация структуры "пустой" коллекции для генерации документа на основе описания столбцов отчета
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return описание коллекции json-объектов
   */
  private Set< Map<String, Object>> generateEmptyList(ReportDescriptor descriptor) {
    //считаем что начальное состояние у всех объектов-строк одинаковое
    final Map<String, Number> emptyValue = generateEmptyValue(descriptor);

    Set<Map<String, Object>> result = new HashSet<Map<String, Object>>();
    if (descriptor.getLines() != null)
      for (ReportLine line : descriptor.getLines()) {
        Map<String, Object> entry = new HashMap<String, Object>(2);
        entry.put("value", emptyValue);
        entry.put("_id", line.getId());
        result.add(entry);
      }
    return result;
  }

}
