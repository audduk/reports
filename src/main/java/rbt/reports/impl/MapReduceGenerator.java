package rbt.reports.impl;

import rbt.reports.entities.*;

import java.util.*;

/**
 * ОПК.5.3.2. Управление документами регламентированной отчетности
 * Реализация бизнес-логики управления отчетами, вспомогательные методы генерации запроса MapReduce на основе описателя
 */
public final class MapReduceGenerator {
  public static class Result {
    private Map<String, Object> scope = null;
    private String map;
    private String reduce;

    public Map<String, Object> getScope() {
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
    final Generator gen = new Generator(descriptor);
    Result result = new Result();
    result.map = gen.mapFunction();
    result.reduce = gen.reduceFunction();
    return result;
  }

  private static class Generator {
    //установленное соответствие между строками для построения строк таблицы типа SUBTOTAL
    private Map<String,String> lineEq = new HashMap<>();
    //строка таблицы типа TOTAL, должна быть единственной в рамках таблицы
    private String totalLine = null;
    //описатель таблицы, для которого выполняется генерация
    private TableDescriptor descriptor;
    // Наличие идентификатора базового документа является так же признаком наличия поля drilldown
    private String documentColumn;

    public Generator(TableDescriptor descriptor) {
      this.descriptor = descriptor;
      this.documentColumn = descriptor.getDocumentColumn();
      prepareLines(descriptor.getLines(), descriptor.getTable());
    }

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

    public String mapFunction() {
      StringBuilder bf = new StringBuilder("function (){\n");
      bf.append(" var _value = null;\n");
      // тестовый вывод
//      bf.append(" function emit(key, value) { print('emit'); print('key: ' + key + ' value: ' + tojson(value));}\n");

      valueFunctionGenerator(bf);

      for (LineDescriptor line : descriptor.getLines()) {
        if (!LineType.VALUE.equals(line.getType()))
          continue;
        if (line.getDescriptor() == null || "".equals(line.getDescriptor()))
          throw new RuntimeException(descriptor.getTable() + "." + line.getId() + ". Отсутствует условие фильтрации для строки (descriptor)");

        bf.append(String.format(" if(%s){\n", line.getDescriptor()));
        bf.append(String.format("  _value=_valueFunc.call(this);\n")); //call!
        bf.append(String.format("  if(_value!=null){\n"));
        genEmit(bf, line.getId());
        if (lineEq.containsKey(line.getId()))
          genEmit(bf, lineEq.get(line.getId()));
        if (totalLine != null && !"".equals(totalLine))
          genEmit(bf, totalLine);
        bf.append("  }\n");
        bf.append(" }\n");
      }
      bf.append("}");
      return bf.toString();
    }

    private StringBuilder genEmit(StringBuilder bf, String line) {
      return bf.append(String.format("   emit(\"%s\",_value);\n", line));
    }

    private void valueFunctionGenerator(final StringBuilder bf) {
      bf.append(" function _valueFunc(){\n");
      bf.append("  var result={};\n");
      bf.append("  var val;\n");
      forEachColumn(new IApply() {
        @Override
        public void apply(ColumnDescriptor column) {
          bf.append(String.format("  val=%s;\n", column.getDescriptor()));
          bf.append(String.format("  result.%s={value:val,fixed:val%s};\n", column.getId(),
              documentColumn != null ? String.format(",drilldown:val!=0?[this.%s]:[]", documentColumn) : ""));
        }
      });
      bf.append("  return result;\n");
      bf.append(" }\n");
    }

    private static interface IApply {
      void apply(ColumnDescriptor column);
    }

    private void forEachColumn(IApply apply) {
      for (ColumnDescriptor column : descriptor.getColumns()) {
        if (ColumnType.CONST.equals(column.getType()))
          continue; // константные колонки обрабатываются позже, на этапе генераци документа
        if (ColumnType.CALCULATED.equals(column.getType()))
          throw new IllegalArgumentException("ColumnType.CALCULATED unsupported yet!"); //TODO
        if (column.getDescriptor() == null || "".equals(column.getDescriptor()))
          throw new RuntimeException(descriptor.getTable() + "." + column.getId() + ". Отсутствует выражение для колонки (descriptor)");
        apply.apply(column);
      }
    }

    public String reduceFunction() {
      final StringBuilder bf = new StringBuilder("function (key, values){\n");
      bf.append(" var result={};\n");
      forEachColumn(new IApply() {
        @Override
        public void apply(ColumnDescriptor column) {
          bf.append(String.format(" result.%s={value:0,fixed:0%s};\n", column.getId(),
              documentColumn != null ? ",drilldown:[]" : ""));
        }
      });
      bf.append(" for(var idx=values.length-1;idx>=0;--idx){\n");
      bf.append("  var _idxVal=values[idx];\n");
      forEachColumn(new IApply() {
        @Override
        public void apply(ColumnDescriptor column) {
          bf.append(String.format("  result.%s.value+=_idxVal.%s.value;\n", column.getId(), column.getId()));
          bf.append(String.format("  result.%s.fixed+=_idxVal.%s.fixed;\n", column.getId(), column.getId()));
          if (documentColumn != null) {
            bf.append(String.format("  if(_idxVal.%s.value!=0)\n", column.getId()));
            bf.append(String.format("   result.%s.drilldown=result.%s.drilldown.concat(_idxVal.%s.drilldown);\n",
                column.getId(), column.getId(), column.getId()));
          }
        }
      });
      bf.append(" }\n");
      bf.append(" return result;\n");
      bf.append("}");
      return bf.toString();
    }

  }
}