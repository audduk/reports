package rbt.reports.impl;

import rbt.reports.entities.TableDescriptor;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами, вспомогательные методы генерации запроса MapReduce на основе описателя
 */
public class MapReduceGenerator {
  public static class Result {
    public String map;
    public String reduce;
  }

  public static Result generate(TableDescriptor descriptor) {
    return new Result();
  }

}
