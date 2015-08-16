package rbt.reports.entities;


/**
 *
 */
public class ReportLine {
  private String id;
  private ReportLineType type;
  private String descriptor;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ReportLineType getType() {
    return type;
  }

  public void setType(ReportLineType type) {
    this.type = type;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }
}
