package rbt.reports.impl;

import org.springframework.beans.factory.annotation.Value;
import rbt.reports.ReportManager;
import rbt.reports.entities.TableDescriptor;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами
 */
public class ReportManagerImpl implements ReportManager {

  @Value("${contentCollection:content}")
  private String contentCollection = "content";

  private Mongo mongo;

  public ReportManagerImpl(Mongo mongo) {
    this.mongo = mongo;
  }

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
    Map<String, Map<String, Object>> content = generator.emptyCollection(descriptor);
    if (!empty) {
      final String collectionName = docId + "." + descriptor.getTable();
      // сохранить пустую коллекцию в базе (имя коллекции = docId.tableid)
      Mongo.Collection collection = mongo.new Collection(collectionName);
      collection.insertAll(content.values());
      //сформировать MapReduce
      MapReduceGenerator.Result mapReduce = MapReduceGenerator.generate(descriptor);
      //выполнить MapReduce для коллекции (сохраняем результат в коллекцию с именем collectionName)
      Mongo.Collection reduceCollection = mongo.new Collection(descriptor.getCollection());
      reduceCollection.mapReduce(mapReduce.getMap(), mapReduce.getReduce(), null, collectionName);
      //загрузить результат из заполненной коллекции в переменную content
      // ...
    }
    //выполнить копирование данных в коллекцию документов
    Collection<Map<String, Object>> documentCollection = generator.copyToDocument(descriptor, docId, content);
    //выполнить сохранение документа в базу данных
    //...
    return docId;
  }

}
