package rbt.reports.impl;

import com.google.gson.Gson;
import rbt.reports.entities.*;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами, вспомогательные методы
 */
public class GeneratorUtils {

  private String dbName = "db";

  public GeneratorUtils(String dbName) {
    this.dbName = dbName;
  }

  /**
   * Генерация набора команд для создания начального (пустого) содержания коллекции
   * @param docId идентификатор документа (совпадает с именем коллекции в БД)
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return команды по заполнению коллекции
   */
  @Deprecated
  public List<String> initialCollection(String docId, ReportDescriptor descriptor) {
    final String collectionPath = dbName + "." + docId + ".insert(";
    Gson gson = new Gson();

    Map<String, Map<String, Object>> emptyList = emptyCollection(descriptor);
    List<String> result = new ArrayList<String>(emptyList.size());
    for (Map.Entry<String, Map<String, Object>> entry : emptyList.entrySet())
      result.add(collectionPath + gson.toJson(entry.getValue()) + ")");
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
  public Map<String, Map<String, Object>> emptyCollection(ReportDescriptor descriptor) {
    //считаем что начальное состояние у всех объектов-строк одинаковое
    final Map<String, Number> emptyValue = generateEmptyValue(descriptor);

    Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
    if (descriptor.getLines() != null)
      for (ReportLine line : descriptor.getLines()) {
        Map<String, Object> entry = new HashMap<String, Object>(2);
        entry.put("value", emptyValue);
        entry.put("_id", line.getId());
        result.put(line.getId(), entry);
      }
    return result;
  }

}
