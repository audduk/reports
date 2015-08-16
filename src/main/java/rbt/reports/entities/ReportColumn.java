package rbt.reports.entities;


/**
 * Описатель столбца регламентированного отчета
 */
public class ReportColumn {
  private String id;
  private String name;
  private ReportColumnType type;
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

  public ReportColumnType getType() {
    return type;
  }

  public void setType(ReportColumnType type) {
    this.type = type;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }
}
