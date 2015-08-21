package rbt.reports.entities;

import java.util.List;

/**
 * Описатель таблицы регламентированного отчета
 */
public class TableDescriptor {
  /** Наименование (идентификатор) таблицы в рамках отчета */
  private String table;
  /** Читаемое название таблицы (для построения интерфейса пользователя */
  private String name;
  /** Наименование базовой коллекции */
  private String collection;
  /** Наименование столбца, содержащего идентификатор базового (исходного) документа для построения drilldown */
  private String documentColumn;
  /** Описатели строк таблицы */
  private List<LineDescriptor> lines;
  /** Описатели колонок таблицы */
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

  public String getDocumentColumn() {
    return documentColumn;
  }

  public void setDocumentColumn(String documentColumn) {
    this.documentColumn = documentColumn;
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
