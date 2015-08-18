package rbt.reports.impl;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * Реализация логики работы с базой MongoDB
 */
public class WorkWithMongo implements Closeable {
  // это клиент который обеспечит подключение к БД
  private MongoClient mongoClient;

  // возможность аутентифицироваться в MongoDB
  private DB db;

  // тут мы будем хранить состояние подключения к БД
  private boolean authenticate;

  // И класс который обеспечит возможность работать с коллекциями MongoDB
  private DBCollection collection;

  public WorkWithMongo(Properties prop) {
    try {
      // Создаем подключение
      mongoClient = new MongoClient( prop.getProperty("host"), Integer.valueOf(prop.getProperty("port")) );

      // Выбираем БД для дальнейшей работы
      db = mongoClient.getDB(prop.getProperty("dbname"));

      // Входим под созданным логином и паролем
      if (prop.getProperty("login") != null)
        authenticate = db.authenticate(prop.getProperty("login"), prop.getProperty("password").toCharArray());

      // Выбираем коллекцию/таблицу для дальнейшей работы
      collection = db.getCollection(prop.getProperty("collection"));

    } catch (UnknownHostException e) {
      // Если возникли проблемы при подключении сообщаем об этом
      System.err.println("Don't connect!");
    }
  }

  public void insert(Map<String, Object> obj){
    BasicDBObject document = new BasicDBObject(obj);
    collection.insert(document);
  }

  public void insertAll(Collection<Map<String, Object>> objects){
    for (Map<String, Object> obj : objects)
      insert(obj);
  }

  public void select() {
    collection.find();
  }

  @Override
  public void close() throws IOException {
    if (mongoClient != null)
      mongoClient.close();
  }
}
