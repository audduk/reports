package rbt.reports;

import com.google.javascript.jscomp.parsing.parser.Parser;
import com.google.javascript.jscomp.parsing.parser.SourceFile;
import com.google.javascript.jscomp.parsing.parser.util.ErrorReporter;
import com.google.javascript.jscomp.parsing.parser.util.SourcePosition;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import rbt.reports.descriptors.entities.TableDescriptor;
import rbt.reports.impl.MapReduceGenerator;

/**
 * Тестирование алгоритмов генерации
 */
public class AlgorithmGenerationTest extends AbstractGeneration {
  /**
   * Эталонный описатель отчета
   */
  TableDescriptor desc;

  @Before
  public void before() {
    desc = readDescriptor("test.json");
  }

  /**
   * Проверка основного функционала методов генерации js-функций (map и reduce) - проверка синтаксиса функций
   */
  @Test
  public void generatorResultSyntaxTest() {
    TableDescriptor desc = readDescriptor("test.json");

    MapReduceGenerator.Result result = MapReduceGenerator.generate(desc, "TableID");
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