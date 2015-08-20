package rbt.reports;

import com.google.gson.Gson;
import com.google.javascript.jscomp.parsing.parser.Parser;
import com.google.javascript.jscomp.parsing.parser.SourceFile;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rbt.reports.entities.TableDescriptor;
import rbt.reports.impl.MapReduceGenerator;
import rbt.reports.impl.Mongo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static rbt.reports.impl.GeneratorUtils.newMap;

/**
 * Тестируем загрузку алгоритмов из эталонных json
 */
public class ReportAlgorithmSerializationTest {

  private Mongo mongo;

  @Before
  public void before() {
    String url = getClass().getClassLoader().getResource("mongo.prop").getFile();
    File file = new File(url);
    try {
      FileReader fr = new FileReader(file);
      Properties prop = new Properties();
      prop.load(fr);
      mongo = new Mongo(prop);
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

  /**
   * Отладка работы с MongoDB - не для автоматического тестирования
   */
  @Test
  @Ignore
  public void initialCollectionTest() {
    TableDescriptor desc = readDescriptor("test.json");

    List<Map<String, Object>> testCollection = new ArrayList<Map<String, Object>>(10);
    testCollection.add(newMap("age", 1, "mkb", "A01"));
    testCollection.add(newMap("age", 10, "mkb", "A01"));
    testCollection.add(newMap("age", 18, "mkb", "A01"));
    testCollection.add(newMap("age", 18, "mkb", "I01"));
    testCollection.add(newMap("age", 17, "mkb", "I01"));

    MapReduceGenerator.Result result = MapReduceGenerator.generate(desc);

    Mongo.Collection collection = mongo.new Collection("test.collection");
    collection.insertAll(testCollection);
    collection.mapReduce(result.getMap(), result.getReduce(), null, "test.mapreduce");

//    Map<String, Map<String, Object>> emptyCollection = GeneratorUtils.emptyCollection(desc);
//    Mongo.Collection collection = mongo.new Collection("rrr");
//    collection.insertAll(emptyCollection.values());
  }

  /**
   * Проверка основного функционала методов генерации js-функций (map и reduce) - проверка синтаксиса функций
   */
  @Test
  public void generatorResultSyntaxTest() {
    TableDescriptor desc = readDescriptor("test.json");

    MapReduceGenerator.Result result = MapReduceGenerator.generate(desc);
    //выполняем проверку синтаксиса функции map
    System.out.println(result.getMap());
    checkSyntax(result.getMap());
    //выполняем проверку синтаксиса функции reduce
    System.out.println(result.getReduce());
    checkSyntax(result.getReduce());
  }

  /**
   * Проверка синтаксиса JavaScript-функции
   * @param code код проверяемой функции
   */
  private void checkSyntax(String code) {
    code = code.replace("function (", "function _("); //замена для обеспечения работы Parser

    final Reporter errorReporter = new Reporter();
    Parser parser = new Parser(
        new Parser.Config(Parser.Config.Mode.ES3),
        errorReporter,
        new SourceFile("", code));
    parser.parseProgram();
    Assert.assertTrue("Некорректный синтаксис функции", errorReporter.getNumErrors() == 0);
    System.out.println("--- Check finished ---");
  }

  /**
   * Накопление информации о синтаксических ошибках
   */
  private static class Reporter extends ErrorReporter {
    private Integer numErrors = 0;
    private Integer numWarnings = 0;

    @Override
    protected void reportError(SourcePosition location, String message) {
      ++numErrors;
      System.out.println("ERROR: " + location.line + ":" + location.column + " - " + message);
    }

    @Override
    protected void reportWarning(SourcePosition location, String message) {
      ++numWarnings;
      System.out.println("WARNING: " + location.line + ":" + location.column + " - " + message);
    }

    public Integer getNumErrors() {
      return numErrors;
    }

    public Integer getNumWarnings() {
      return numWarnings;
    }
  }

}