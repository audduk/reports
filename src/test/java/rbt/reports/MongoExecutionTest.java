package rbt.reports;

import org.junit.*;
import rbt.reports.descriptors.entities.TableDescriptor;
import rbt.reports.impl.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static rbt.reports.impl.GeneratorUtils.newMap;

/**
 * Отладка работы с MongoDB - не для автоматического тестирования
 */
public class MongoExecutionTest extends AbstractGeneration {

  private Mongo mongo;
  /**
   * Эталонный описатель отчета
   */
  private TableDescriptor desc;
  /**
   * Эталонный описатель отчета
   */
  private final String table = "TableID";
  /**
   * Тестовая коллекция mongo (заполнена эталонными тестовыми данными)
   */
  private Mongo.Collection collection;

  @Before
  public void before() {
    File file = getResourceFile("mongo.prop");
    try {
      FileReader fr = new FileReader(file);
      Properties prop = new Properties();
      prop.load(fr);
      mongo = new Mongo(prop);
    } catch (IOException e) {
      e.printStackTrace();
    }

    desc = readDescriptor("test.json");

    collection = mongo.new Collection(desc.getCollection());
    if (collection.select().size() == 0) {
      List<Map<String, Object>> testCollection = new ArrayList<>(10);
      //по фильтру - "line2"
      testCollection.add(newMap("age", 1,  "mkb", "A01", "caseId", "c1"));
      testCollection.add(newMap("age", 10, "mkb", "A01", "caseId", "c2"));
      testCollection.add(newMap("age", 18, "mkb", "A01", "caseId", "c3"));
      //по фильтру - "line1"
      testCollection.add(newMap("age", 18, "mkb", "I01", "caseId", "c4"));
      testCollection.add(newMap("age", 17, "mkb", "I01", "caseId", "c5"));

      collection.insertAll(testCollection);
    }
  }

  @After
  public void after() {
    try {
      if (mongo != null)
        mongo.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  @Ignore
  /**
   * Тестируем выполнение сгенерированного на основе эталонного описателя процесса MapReduce
   */
  public void mapReduceTest() {
    MapReduceGenerator.Result mapReduce = MapReduceGenerator.generate(desc, table);
    Mongo.Collection resultCollection = collection.mapReduce(mapReduce.getMap(), mapReduce.getReduce(), mapReduce.getScope(), "__reduce");
    // проверяем полученный результат
    Collection<Map<String, Object>> resultData = resultCollection.select();
    for(Map<String, Object> entry : resultData) {
      final Map<String, Map> value = (Map<String, Map>) entry.get("value");
      if (entry.get("_id").equals("line1")) {
        Assert.assertEquals(1.0, value.get("younger").get("value"));
        Assert.assertEquals(1.0, value.get("elder").get("value"));
      }
      if (entry.get("_id").equals("line2") || entry.get("_id").equals("subtotalLine1")) { //по описанию совпадают
        Assert.assertEquals(2.0, value.get("younger").get("value"));
        Assert.assertEquals(1.0, value.get("elder").get("value"));
      }
      if (entry.get("_id").equals("totalLine")) {
        Assert.assertEquals(3.0, value.get("younger").get("value"));
        Assert.assertEquals(2.0, value.get("elder").get("value"));
      }
    }
  }

  @Test
  @Ignore
  /**
   * Тестируем выполнение генерации коллекции содержимого отчетов (на основе эталонного описателя)
   */
  public void reportTableGenerationTest() {
    ReportManagerImpl manager = new ReportManagerImpl(mongo, null);
    String filledReport = manager.generateReportTable(UUID.randomUUID().toString().toLowerCase(), table, desc);
    System.out.println(String.format("Report table '%s' generated in '%s' collection", filledReport, manager.getContentCollection()));
    System.out.println(String.format("\tMapReduce collection - '%s.%s'", filledReport, table));
  }

  @Test
  @Ignore
  /**
   * Тестируем выполнение генерации пустой коллекции (на основе эталонного описателя)
   */
  public void emptyTableGenerationTest() {
    ReportManagerImpl manager = new ReportManagerImpl(mongo, null);
    String emptyReport = manager.generateEmptyReportTable(UUID.randomUUID().toString().toLowerCase(), table, desc);
    System.out.println(String.format("Empty report table '%s' generated in '%s' collection", emptyReport, manager.getContentCollection()));
  }

}