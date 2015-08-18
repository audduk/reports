package rbt.reports.entities;


/**
 * Описатель столбца таблицы регламентированного отчета
 */
public class ColumnDescriptor {
  private String id;
  private String name;
  private ColumnType type;
  private String descriptor;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ColumnType getType() {
    return type;
  }

  public void setType(ColumnType type) {
    this.type = type;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }
}
