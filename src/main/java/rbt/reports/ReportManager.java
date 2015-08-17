package rbt.reports;

import rbt.reports.entities.ReportDescriptor;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Бизнес-логика управления отчетами
 */
public interface ReportManager {
  /**
   * Генерация отчета и сохранение в хранилище.
   * Генерация пустого отчета по описателю.
   * @return идентификатор (uid) сгенерированного отчета, сохраненного в хранилище
   */
  String generateEmptyDocument(ReportDescriptor descriptor);

  /**
   * Генерация отчета и сохранение в хранилище.
   * Генерация отчета по описателю на основе данных аналитической базы.
   * @return идентификатор (uid) сгенерированного отчета, сохраненного в базе данных
   */
  String generateDocument(ReportDescriptor descriptor);
}
