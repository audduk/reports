package rbt.reports;

import com.google.gson.Gson;
import rbt.reports.descriptors.entities.TableDescriptor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Методы для использования тестами
 */
public abstract class AbstractGeneration {

  protected final File getResourceFile(String fileName) {
    return new File(getClass().getClassLoader().getResource(fileName).getFile());
  }

  /**
   * Загружает описатель таблицы отчета из файла-ресурса
   * @param fileName имя файла
   * @return загруженнй описатель таблицы отчета
   */
  protected final TableDescriptor readDescriptor(String fileName) {
    File file = getResourceFile(fileName);
    try {
      FileReader fr = new FileReader(file);
      Gson gson = new Gson();
      TableDescriptor result = gson.fromJson(fr, TableDescriptor.class);
      fr.close();
      return result;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}
