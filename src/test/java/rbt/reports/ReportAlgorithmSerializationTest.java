package rbt.reports;

import com.google.gson.Gson;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import rbt.reports.entities.*;
import rbt.reports.impl.GeneratorUtils;
import rbt.reports.impl.WorkWithMongo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

  @Test
  public void reportAlgorithmSerializationTest() {
    ReportDescriptor desc = new ReportDescriptor();
    desc.setName("Test");
    desc.setCollection("collection");

    ReportLine line = new ReportLine();
    line.setId("line1Id");
    line.setType(ReportLineType.VALUE);
    line.setDescriptor("line1\nDescription");
    desc.setLines(new ArrayList<ReportLine>(1));
    desc.getLines().add(line);

    ReportColumn column = new ReportColumn();
    column.setId("col1Id");
    column.setName("col1Name");
    column.setType(ReportColumnType.CONST);
    column.setDescriptor("col1Descriptor");
    desc.setColumns(new ArrayList<ReportColumn>(1));
    desc.getColumns().add(column);

    Gson gson = new Gson();
    String result = gson.toJson(desc);
    System.out.println(result);
  }

  private ReportDescriptor readDescriptor(String resourceFile) {
    String url = getClass().getClassLoader().getResource(resourceFile).getFile();
    File file = new File(url);
    try {
      FileReader fr = new FileReader(file);
      Gson gson = new Gson();
      ReportDescriptor result = gson.fromJson(fr, ReportDescriptor.class);
      fr.close();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Test
  public void initialCollectionTest() {
    ReportDescriptor desc = readDescriptor("test.json");

    GeneratorUtils generator = new GeneratorUtils("db");

    Map<String, Map<String, Object>> emptyCollection = generator.emptyCollection(desc);
    mongo.insertAll(emptyCollection.values());

    List<String> collection = generator.initialCollection("docId", desc);
    for (String entry : collection)
      System.out.println(entry);
  }
}