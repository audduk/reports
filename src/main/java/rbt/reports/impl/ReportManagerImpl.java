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

  @Value("${contentCollection:content}")
  private String documentCollection = "documents";

  private Mongo mongo;

  public ReportManagerImpl(Mongo mongo) {
    this.mongo = mongo;
  }

  public String getContentCollection() {
    return contentCollection;
  }

  public String getDocumentCollection() {
    return documentCollection;
  }

  @Override
  public String generateEmptyReportTable(String docId, String table, TableDescriptor descriptor) {
    Map<String, Map<String, Object>> content = GeneratorUtils.emptyCollection(descriptor);
    return saveDocumentContent(docId, table, descriptor, content);
  }

  @Override
  public String generateReportTable(String docId, String table, TableDescriptor descriptor) {
    //выполняем MapReduce для коллекции (сохраняем результат в коллекцию с именем docId.tableId)
    MapReduceGenerator.Result mapReduce = MapReduceGenerator.generate(descriptor, table);
    Mongo.Collection collection = mongo.new Collection(descriptor.getCollection());
    Mongo.Collection result = collection.mapReduce(mapReduce.getMap(), mapReduce.getReduce(), mapReduce.getScope(),
        docId + "." + table);

    //загружаем результат из заполненной коллекции и переместить в документы
    Collection<Map<String, Object>> resultData = result.select();
    //преобразуем загруженный результат в структуру content
    Map<String, Map<String, Object>> content = new HashMap<>(resultData.size());
    for (Map<String, Object> entry : resultData)
      content.put((String) entry.get("_id"), entry); // ожидается, что _id - это именно строка, в противном случае - ошибка!
    return saveDocumentContent(docId, table, descriptor, content);
  }

  private String saveDocumentContent(String docId, String table, TableDescriptor descriptor, Map<String, Map<String, Object>> content) {
    Collection<Map<String, Object>> documentCollection = GeneratorUtils.copyToDocument(descriptor, docId, table, content);
    Mongo.Collection collection = mongo.new Collection(contentCollection);
    collection.insertAll(documentCollection);
    return docId;
  }

}
