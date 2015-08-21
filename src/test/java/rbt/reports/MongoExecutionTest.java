package rbt.reports;

import org.junit.*;
import rbt.reports.entities.TableDescriptor;
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
    if (collection.select(new HashMap<String, Object>()).size() == 0) {
      List<Map<String, Object>> testCollection = new ArrayList<>(10);
      testCollection.add(newMap("age", 1,  "mkb", "A01", "caseId", "c1"));
      testCollection.add(newMap("age", 10, "mkb", "A01", "caseId", "c2"));
      testCollection.add(newMap("age", 18, "mkb", "A01", "caseId", "c3"));
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
    MapReduceGenerator.Result result = MapReduceGenerator.generate(desc);
    Mongo.Collection reduceResult = collection.mapReduce(result.getMap(), result.getReduce(), null, "reduce");
  }

  @Test
  @Ignore
  /**
   * Тестируем выполнение процессов генерации коллекции содержимого отчетов (на основе эталонного описателя)
   */
  public void reportTableGenerationTest() {
    ReportManagerImpl manager = new ReportManagerImpl(mongo);

    String emptyReport = manager.generateEmptyReportTable(UUID.randomUUID().toString().toLowerCase(), desc);
    System.out.println(String.format("Empty report table '%s' generated in '%s' collection", emptyReport, manager.getContentCollection()));

    String filledReport = manager.generateReportTable(UUID.randomUUID().toString().toLowerCase(), desc);
    System.out.println(String.format("Report table '%s' generated in '%s' collection", filledReport, manager.getContentCollection()));
    System.out.println(String.format("\tMapReduce collection - '%s.%s'", filledReport, desc.getTable()));
  }

}