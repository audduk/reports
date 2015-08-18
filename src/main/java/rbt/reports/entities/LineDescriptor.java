package rbt.reports.entities;


/**
 * Описатель строки таблицы регламентированного отчета
 */
public class LineDescriptor {
  private String id;
  private LineType type;
  private String descriptor;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public LineType getType() {
    return type;
  }

  public void setType(LineType type) {
    this.type = type;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }
}
