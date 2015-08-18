package rbt.reports.impl;

import rbt.reports.ReportManager;
import rbt.reports.entities.TableDescriptor;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами
 */
public class ReportManagerImpl implements ReportManager {

  @Override
  public String generateEmptyReportTable(String docId, TableDescriptor descriptor) {
    return internalGenerateDocument(docId, descriptor, true);
  }

  @Override
  public String generateReportTable(String docId, TableDescriptor descriptor) {
    return internalGenerateDocument(docId, descriptor, false);
  }

  private String internalGenerateDocument(String docId, TableDescriptor descriptor, Boolean empty) {
    GeneratorUtils generator = new GeneratorUtils();
    //сформировать пустую коллекцию
    Map<String, Map<String, Object>> collection = generator.emptyCollection(descriptor);
    // -- сохранить пустую коллекцию в базе (имя коллекции - docId)
    if (!empty) {
      //сформировать MapReduce
      //выполнить MapReduce для коллекции
    }
    //выполнить копирование данных в коллекцию документов
    Collection<Map<String, Object>> documentCollection = generator.copyToDocument(descriptor, docId, collection);
    //выполнить сохранение документа в базу данных
    //...
    return docId;
  }

}
