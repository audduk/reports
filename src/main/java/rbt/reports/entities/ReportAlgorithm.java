package rbt.reports.entities;

import java.util.List;

/**
 * Описатель регламентированного отчета
 */
public class ReportAlgorithm {
  private String name;
  private String collection;
  private List<ReportLine> lines;
  private List<ReportColumn> columns;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getCollection() {
    return collection;
  }

  public void setCollection(String collection) {
    this.collection = collection;
  }

  public List<ReportLine> getLines() {
    return lines;
  }

  public void setLines(List<ReportLine> lines) {
    this.lines = lines;
  }

  public List<ReportColumn> getColumns() {
    return columns;
  }

  public void setColumns(List<ReportColumn> columns) {
    this.columns = columns;
  }
}
