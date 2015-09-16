package rbt.reports.descriptors.entities;

/**
 * Тип столбца таблицы регламентированного отчета: константа, расчетный, агрегат;
 */
public enum ColumnType {
  /* Тип показателя - константа */
  CONST,
  /* Тип показателя - расчетный */
  VALUE,
  /* Тип показателя - агрегат */
  CALCULATED
}
