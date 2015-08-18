package rbt.reports;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rbt.reports.entities.TableDescriptor;
import rbt.reports.impl.GeneratorUtils;
import rbt.reports.impl.WorkWithMongo;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * Тестируем загрузку алгоритмов из эталонных json
 */
public class ReportAlgorithmSerializationTest {

  private WorkWithMongo mongo;

  @Before
  public void before() {
    String url = getClass().getClassLoader().getResource("mongo.prop").getFile();
    File file = new File(url);
    try {
      FileReader fr = new FileReader(file);
      Properties prop = new Properties();
      prop.load(fr);
      mongo = new WorkWithMongo(prop);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
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

  private TableDescriptor readDescriptor(String resourceFile) {
    String url = getClass().getClassLoader().getResource(resourceFile).getFile();
    File file = new File(url);
    try {
      FileReader fr = new FileReader(file);
      Gson gson = new Gson();
      TableDescriptor result = gson.fromJson(fr, TableDescriptor.class);
      fr.close();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Test
  public void initialCollectionTest() {
    TableDescriptor desc = readDescriptor("test.json");

    GeneratorUtils generator = new GeneratorUtils();
    Map<String, Map<String, Object>> emptyCollection = generator.emptyCollection(desc);
    mongo.insertAll(emptyCollection.values());
  }
}