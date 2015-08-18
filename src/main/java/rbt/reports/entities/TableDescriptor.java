package rbt.reports.entities;

import java.util.List;

/**
 * Описатель таблицы регламентированного отчета
 */
public class TableDescriptor {
  private String table;
  private String name;
  private String collection;
  private List<LineDescriptor> lines;
  private List<ColumnDescriptor> columns;

  public String getTable() {
    return table;
  }

  public void setTable(String table) {
    this.table = table;
  }

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

  public List<LineDescriptor> getLines() {
    return lines;
  }

  public void setLines(List<LineDescriptor> lines) {
    this.lines = lines;
  }

  public List<ColumnDescriptor> getColumns() {
    return columns;
  }

  public void setColumns(List<ColumnDescriptor> columns) {
    this.columns = columns;
  }
}
