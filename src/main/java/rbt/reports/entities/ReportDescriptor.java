package rbt.reports.entities;

import java.util.List;

/**
 * Описатель регламентированного отчета (как совокупности таблиц)
 */
public class ReportDescriptor {
  /**
   * Служебный идентификатор, для организации ссылок
   */
  String _id;
  /**
   * Версия описателя, не заполняется если версия единственная
   */
  Integer version;
  /**
   * Наименование документа
   */
  String name;
  /**
   * Шаблон (имя файла шаблона)
   */
  String template;
  /**
   * Состав документа (массив описателей таблиц)
   */
  List<Table> tables;

  /**
   * Описание таблицы в структуре описания отчета
   */
  public static class Table {
    /**
     * Идентификатор таблицы (в рамках отчета)
     */
    private String id;
    /**
     * Наименование таблицы
     */
    private String name;
    /**
     * Ссылка на алгоритм (идентификатор алгоритма)
     */
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

    public String getDescriptor() {
      return descriptor;
    }

    public void setDescriptor(String descriptor) {
      this.descriptor = descriptor;
    }
  }

  public String get_id() {
    return _id;
  }

  public void set_id(String _id) {
    this._id = _id;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTemplate() {
    return template;
  }

  public void setTemplate(String template) {
    this.template = template;
  }

  public List<Table> getTables() {
    return tables;
  }

  public void setTables(List<Table> tables) {
    this.tables = tables;
  }
}
