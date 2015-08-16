package rbt.reports.entities;

/**
 * Тип показателя (столбца регламентированного отчета): константа, расчетный, агрегат;
 */
public enum ReportColumnType {
  /* Тип показателя - константа */
  CONST,
  /* Тип показателя - расчетный */
  CALCULATED,
  /* Тип показателя - агрегат */
  AGGREGATE
}
