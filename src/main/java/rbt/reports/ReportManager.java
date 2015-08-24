package rbt.reports;

import rbt.reports.entities.ReportDescriptor;
import rbt.reports.entities.TableDescriptor;

import java.util.Collection;
import java.util.Map;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Бизнес-логика управления отчетами
 */
public interface ReportManager {

  /**
   * Генерация отчета по описателю
   * @param descriptor описатель отчета
   * @return идентификатор отчета, сохраненного в хранилище
   */
  String generateReport(ReportDescriptor descriptor);

  /**
   * Генерация пустой таблицы отчета по описателю.
   * @param docId идентификатор отчета, для которого выполняется генерация
   * @param table идентификатор таблицы отчета, для которой выполняется генерация
   * @param descriptor описатель таблицы отчета
   * @return идентификатор (uid) сгенерированного отчета, сохраненного в хранилище
   */
  String generateEmptyReportTable(String docId, String table, TableDescriptor descriptor);

  /**
   * Генерация таблицы отчета по описателю на основе данных аналитической базы.
   * @param docId идентификатор отчета, для которого выполняется генерация
   * @param table идентификатор таблицы отчета, для которой выполняется генерация
   * @param descriptor описатель таблицы отчета
   * @return идентификатор (uid) сгенерированного отчета, сохраненного в базе данных
   */
  String generateReportTable(String docId, String table, TableDescriptor descriptor);

  /**
   * Получить данные таблицы отчета из хранилища
   * @param docId идентификатор отчета
   * @param table идентификатор таблицы в составе отчета
   * @return данные таблицы отчета
   */
  Collection<Map<String, Object>> getReportTableData(String docId, String table);
}
