package rbt.reports;

import rbt.reports.entities.ReportDescriptor;

import java.util.List;

/**
 * Подсистема генерации ОПК.5.3.2
 */
public interface ReportsGenerator {
  /**
   * Генерация набора команд для создания начального (пустого) содержания коллекции
   * @param docId идентификатор документа (совпадает с именем коллекции в БД)
   * @param descriptor описание отчета, для которого выполняем генерация.
   * @return команды по заполнению коллекции
   */
  List<String> initialCollectionCommands(String docId, ReportDescriptor descriptor);
}
