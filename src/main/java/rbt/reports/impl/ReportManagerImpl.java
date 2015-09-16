package rbt.reports.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rbt.reports.ReportManager;
import rbt.reports.descriptors.DescriptorRepository;
import rbt.reports.descriptors.entities.LineDescriptor;
import rbt.reports.descriptors.entities.ReportDescriptor;
import rbt.reports.descriptors.entities.TableDescriptor;

import java.util.*;

import static rbt.reports.impl.GeneratorUtils.newMap;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами
 */
@Service
public class ReportManagerImpl implements ReportManager {

//  @Value("${contentCollection:content}")
  private String contentCollection = "content";

//  @Value("${contentCollection:content}")
  private String documentCollection = "documents";

  private Mongo mongo;
  private DescriptorRepository repository;

  @Autowired
  public ReportManagerImpl(Mongo mongo, DescriptorRepository repository) {
    this.mongo = mongo;
    this.repository = repository;
  }

  public String getContentCollection() {
    return contentCollection;
  }

  public String getDocumentCollection() {
    return documentCollection;
  }

  @Override
  public String generateReport(ReportDescriptor descriptor) {
    final String docId = UUID.randomUUID().toString().toLowerCase();

    return docId;
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
            getCollectionName(docId, table));

    //загружаем результат из заполненной коллекции и переместить в документы
    Collection<Map<String, Object>> resultData = result.select();
    //преобразуем загруженный результат в структуру content
    Map<String, Map<String, Object>> content = new HashMap<>(resultData.size());
    for (Map<String, Object> entry : resultData)
      content.put((String) entry.get("_id"), entry); // ожидается, что _id - это именно строка, в противном случае - ошибка!
    return saveDocumentContent(docId, table, descriptor, content);
  }

  @Override
  public Collection<Map<String, Object>> getReportTableData(String docId, String table) {
    Mongo.Collection collection = mongo.new Collection(contentCollection);
    Collection<Map<String, Object>> result = collection.select(newMap("doc", docId, "table", table));

    //TODO Необходимо релизовать логику работы с отчетом и его описателем

    //заполняем промежуточное представление, для облегчения механизма обработки описателя
    Map<String, Map<String, Object>> content = new HashMap<>(result.size());
    for (Map<String, Object> entry : result)
      content.put((String) entry.get("line"), entry);

    ReportDescriptor reportDesc = repository.getReportDescriptor(docId);
    TableDescriptor tableDesc = repository.getTableDescriptor(table);

    result = new ArrayList<>(result.size());
    for (int i = 0; i < tableDesc.getLines().size(); ++i) {
      final LineDescriptor line = tableDesc.getLines().get(i);
      final Map<String, Object> value = content.get(line.getId()); //содержимое строки
      if (value == null || !value.containsKey("value"))
        throw new RuntimeException("Коллекция не соответствует описателю отчета. Отсутствует строка " + line.getId());
    }
    return result;
  }

  private static String getCollectionName(String docId, String table) {
    return docId + "." + table;
  }

  private String saveDocumentContent(String docId, String table, TableDescriptor descriptor, Map<String, Map<String, Object>> content) {
    Collection<Map<String, Object>> documentCollection = GeneratorUtils.copyToDocument(descriptor, docId, table, content);
    Mongo.Collection collection = mongo.new Collection(contentCollection);
    collection.insertAll(documentCollection);
    return docId;
  }

}
