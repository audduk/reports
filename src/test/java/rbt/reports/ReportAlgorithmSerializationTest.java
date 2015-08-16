package rbt.reports;

import com.google.gson.Gson;
import org.junit.Test;
import rbt.reports.entities.*;

import java.util.ArrayList;

/**
 * Тестируем загрузку алгоритмов из эталонных json
 */
public class ReportAlgorithmSerializationTest {

  @Test
  public void reportAlgorithmSerializationTest() {
    ReportAlgorithm algo = new ReportAlgorithm();
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

  @Test
  public void reportAlgorithmDeserializationTest() {

  }
}