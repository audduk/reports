package rbt.reports.entities;

import java.util.Map;

/**
 * Регламентированный отчет
 */
public class Report {
  private String _id;
  private String descriptor;
  private String name;
  private Map<String, Object> filter;

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public String getDescriptor() {
    return descriptor;
  }

  public void setDescriptor(String descriptor) {
    this.descriptor = descriptor;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Map<String, Object> getFilter() {
    return filter;
  }

  public void setFilter(Map<String, Object> filter) {
    this.filter = filter;
  }
}
