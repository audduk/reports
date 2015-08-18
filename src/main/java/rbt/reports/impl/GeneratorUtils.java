package rbt.reports.impl;

import rbt.reports.entities.*;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами, вспомогательные методы генерации коллекций объектов
 */
public class GeneratorUtils {

  /**
   * Генерация структуры "пустого" объекта на основе описания столбцов отчета
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return описание требуемого объекта
   */
  private Map<String, Object> generateEmptyValue(TableDescriptor descriptor) {
    Map<String, Object> result = new HashMap<String, Object>();
    if (descriptor.getColumns() != null)
      for (ColumnDescriptor column : descriptor.getColumns())
        result.put(column.getId(), newMap("value", 0, "fixed", 0, "drilldown", new ArrayList<String>()));
    return result;
  }

  /**
   * Генерация структуры "пустой" коллекции для генерации документа на основе описания столбцов отчета
   * @param descriptor описание отчета, для которого выполняем генерация
   * @return описание коллекции объектов
   */
  public Map<String, Map<String, Object>> emptyCollection(TableDescriptor descriptor) {
    //считаем, что начальное состояние у всех объектов-строк одинаковое
    final Map<String, Object> emptyValue = generateEmptyValue(descriptor);

    Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>();
    if (descriptor.getLines() != null)
      for (LineDescriptor line : descriptor.getLines()) {
        Map<String, Object> entry = new HashMap<String, Object>(2);
        entry.put("value", emptyValue);
        entry.put("_id", line.getId());
        result.put(line.getId(), entry);
      }
    return result;
  }

  /**
   * Выполняет копирование из промежуточной коллекции в коллекцию документов
   * @param descriptor описатель отчета
   * @param docId идентификатор отчета
   * @param collection промежуточная (расчетная) коллекция
   * @return заполненная коллекция документов, подготовленная для сохранения в базу данных
   */
  public Collection<Map<String, Object>> copyToDocument(TableDescriptor descriptor, String docId,
                                                        Map<String, Map<String, Object>> collection) {
    Collection<Map<String, Object>> result = new ArrayList<Map<String, Object>>(collection.size());
    for (int i = 0; i < descriptor.getLines().size(); ++i) {
      final LineDescriptor line = descriptor.getLines().get(i);
      if (collection.get(line.getId()) == null)
        throw new RuntimeException("Коллекция не соответствует описателю отчета. Отсутствует строка " + line.getId());
      result.add(newMap(
          "doc",    docId,          // идентификатор отчета
          "table",  descriptor.getTable(),  // идентификатор таблицы в рамках отчета
          "line",   line.getId(),   // идентификатор строки в рамках таблицы
          "type",   line.getType(), // тип строки документа
          "number", i,              // номер строки документа по порядку, упорядочение строк в рамках таблицы
          "value",  collection.get(line.getId()))
      );
    }
    return result;
  }
  /**
   * Создаем и заполняем объект типа Map исходными данными
   * @param content содержимое нового объекта, количество аргументов должно быть более 1 и кратным 2
   * @return объект типа Map, заполенный исходными данными
   */
  private Map<String, Object> newMap(Object... content) {
    if (content.length <= 1 || content.length % 2 != 0)
      throw new IllegalArgumentException("Неправильный набор аргументов функции");
    Map<String, Object> result = new HashMap<String, Object>(content.length/2);
    for (int i=0; i < content.length; i+=2)
      result.put((String) content[i], content[i+1]);
    return result;
  }
}
