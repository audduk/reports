package rbt.reports.impl;

import rbt.reports.ReportManager;
import rbt.reports.entities.ReportDescriptor;
import rbt.reports.entities.ReportLine;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами
 */
public class ReportManagerImpl implements ReportManager {

  private String dbName = "db";

  @Override
  public String generateEmptyDocument(ReportDescriptor descriptor) {
    return internalGenerateDocument(descriptor, true);
  }

  @Override
  public String generateDocument(ReportDescriptor descriptor) {
    return internalGenerateDocument(descriptor, false);
  }

  private String internalGenerateDocument(ReportDescriptor descriptor, Boolean empty) {
    String docId = UUID.randomUUID().toString();
    //сформировать пустую коллекцию
    GeneratorUtils generator = new GeneratorUtils(dbName);
    Map<String, Map<String, Object>> collection = generator.emptyCollection(descriptor);
    // -- сохранить пустую коллекцию в базе (имя коллекции - docId)
    if (!empty) {
      //сформировать MapReduce
      //выполнить MapReduce для коллекции
    }
    //выполнить копирование данных в коллекцию документов
    Collection<Map<String, Object>> documentCollection = copyToDocument(descriptor, docId, collection);
    //выполнить сохранение документа в базу данных
    //...
    return docId;
  }

  /**
   * Выполняет копирование из промежуточной коллекции в коллекцию документов
   * @param descriptor описатель отчета
   * @param collection промежуточная (расчетная) коллекция
   * @return заполненная коллекция документов, подготовленная для сохранения в базу данных
   */
  private Collection<Map<String, Object>> copyToDocument(ReportDescriptor descriptor, String docId,
                                                         Map<String, Map<String, Object>> collection) {
    Collection<Map<String, Object>> result = new ArrayList<Map<String, Object>>(collection.size());
    for (int i = 0; i < descriptor.getLines().size(); ++i) {
      final ReportLine line = descriptor.getLines().get(i);
      Map<String, Object> resultLine = new HashMap<String, Object>();
      resultLine.put("docId", docId);          // идентификатор документа
      resultLine.put("line", line.getId());    // идентификатор строки документа
      resultLine.put("type", line.getType());  // тип строки документа
      resultLine.put("number", i);             // номер строки документа по порядку, упорядочение строк в рамках таблицы
      resultLine.put("value", collection.get(line.getId()));

      result.add(resultLine);
    }
    return result;
  }

}
