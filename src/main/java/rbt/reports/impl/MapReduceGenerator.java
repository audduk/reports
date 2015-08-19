package rbt.reports.impl;

import com.google.gson.Gson;
import rbt.reports.entities.*;

import java.util.*;

import static rbt.reports.impl.GeneratorUtils.newMap;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами, вспомогательные методы генерации запроса MapReduce на основе описателя
 */
public final class MapReduceGenerator {
  public static class Result {
    private String scope;
    private String map;
    private String reduce;

    public String getScope() {
      return scope;
    }

    public String getMap() {
      return map;
    }

    public String getReduce() {
      return reduce;
    }
  }

  public static Result generate(TableDescriptor descriptor) {
    Map<String, Object> scope = newMap(
            "filter", generateFilter(descriptor.getLines()));
    Gson gson = new Gson();

    Result result = new Result();
    result.scope = gson.toJson(scope);
    result.map = "";
    result.reduce = "";
    return result;
  }

  @Deprecated
  private static List<Map<String, Object>> generateFilter(List<LineDescriptor> lines) {
    List<Map<String, Object>> result = new ArrayList<Map<String, Object>>(lines.size());
    for (LineDescriptor line : lines) {
      if (line.getDescriptor() == null || "".equals(line.getDescriptor()))
        throw new RuntimeException(line.getId() + ". Отсутствует условие фильтрации (descriptor)");
      result.add(newMap("id", line.getId(), "filter", line.getDescriptor()));
    }
    return result;
  }

  public static class Generator {
    //установленное соответствие между строками для построения строк таблицы типа SUBTOTAL
    final Map<String,String> lineEq = new HashMap<String, String>();
    //строка таблицы типа TOTAL, должна быть единственной в рамках таблицы
    String totalLine = null;

    private void prepareLines(List<LineDescriptor> lines, String table) {
      for (LineDescriptor line : lines) {
        switch (line.getType()) {
          case SUBTOTAL:
            if (line.getDescriptor() == null || "".equals(line.getDescriptor()))
              throw new RuntimeException(table + "." + line.getId() + ". Отсутствует условие фильтрации (descriptor)");

            String[] subtotalLines = line.getDescriptor().replace(";", ",").split(",");
            for (String eq : subtotalLines)
              lineEq.put(eq, line.getId());
            break;
          case TOTAL:
            if (totalLine != null)
              throw new RuntimeException(table + " содержит более одной строки типа TOTAL");
            totalLine = line.getId();
            break;
        }
      }
    }

    public String generateMapFunction(TableDescriptor descriptor) {
      prepareLines(descriptor.getLines(), descriptor.getTable());

      StringBuilder bf = new StringBuilder("function(){\n");
      bf.append("\t").append("var value = null;\n");

      valueFunctionGenerator(bf, descriptor);

      for (LineDescriptor line : descriptor.getLines()) {
        if (!LineType.VALUE.equals(line.getType()))
          continue;
        if (line.getDescriptor() == null || "".equals(line.getDescriptor()))
          throw new RuntimeException(descriptor.getTable() + "." + line.getId() + ". Отсутствует условие фильтрации (descriptor)");

        bf.append("\t").append("if(").append(line.getDescriptor()).append("){").append("\n");
        bf.append("\t\t").append("value=valueFunc();").append("\n");
        bf.append("\t\t").append("if(value!=null){").append("\n");
        genEmit(bf, line.getId());
        if (lineEq.containsKey(line.getId()))
          genEmit(bf, lineEq.get(line.getId()));
        bf.append("\t\t").append("}").append("\n");
        bf.append("\t").append("}").append("\n");
      }
      bf.append("}");
      return bf.toString();
    }

    private StringBuilder genEmit(StringBuilder bf, String line) {
      return bf.append("\t\t\t").append("emit=[\"").append(line).append("\",value);").append("\n");
    }

    private void valueFunctionGenerator(StringBuilder bf, TableDescriptor descriptor) {
      bf.append("\t").append("var valueFunc = function(){\n");
      bf.append("\t").append("}\n");
    }
  }
}