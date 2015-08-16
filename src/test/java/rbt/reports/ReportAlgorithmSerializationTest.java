package rbt.reports;

import com.google.gson.Gson;
import org.junit.Test;
import rbt.reports.entities.*;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Тестируем загрузку алгоритмов из эталонных json
 */
public class ReportAlgorithmSerializationTest {

  @Test
  public void reportAlgorithmSerializationTest() {
    ReportDescriptor algo = new ReportDescriptor();
    algo.setName("Test");
    algo.setCollection("collection");

    ReportLine line = new ReportLine();
    line.setId("line1Id");
    line.setType(ReportLineType.VALUE);
    line.setDescriptor("line1\nDescription");
    algo.setLines(new ArrayList<ReportLine>(1));
    algo.getLines().add(line);

    ReportColumn column = new ReportColumn();
    column.setId("col1Id");
    column.setName("col1Name");
    column.setType(ReportColumnType.CONST);
    column.setDescriptor("col1Descriptor");
    algo.setColumns(new ArrayList<ReportColumn>(1));
    algo.getColumns().add(column);

    Gson gson = new Gson();
    String result = gson.toJson(algo);
    System.out.println(result);
  }

  private ReportDescriptor readAlgorithm(String resourceFile) {
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
  public void reportAlgorithmDeserializationTest() {
    ReportDescriptor algo = readAlgorithm("test.json");
    int i = 0;
  }
}