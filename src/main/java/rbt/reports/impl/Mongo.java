package rbt.reports.impl;

import com.mongodb.*;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Properties;

/**
 * Реализация логики работы с базой MongoDB
 */
public class Mongo implements Closeable {
  // подключение к серверу Mongo
  private MongoClient mongoClient;

  // подключение к БД
  private DB db;

  // тут мы будем хранить состояние подключения к БД
  private boolean authenticate;

  public Mongo(Properties prop) {
    try {
      // Создаем подключение
      mongoClient = new MongoClient( prop.getProperty("host"), Integer.valueOf(prop.getProperty("port")) );

      // Выбираем БД для дальнейшей работы
      db = mongoClient.getDB(prop.getProperty("dbname"));

      // Входим под созданным логином и паролем
      if (prop.getProperty("login") != null)
        authenticate = db.authenticate(prop.getProperty("login"), prop.getProperty("password").toCharArray());

    } catch (UnknownHostException e) {
      // Если возникли проблемы при подключении сообщаем об этом
      System.err.println("Don't connect!");
    }
  }

  public class Collection {
    private DBCollection collection;

    public Collection(String collectionName) {
      collection = db.getCollection(collectionName);
    }

    public void insert(Map<String, Object> obj){
      BasicDBObject document = new BasicDBObject(obj);
      collection.insert(document);
    }

    public void insertAll(java.util.Collection<Map<String, Object>> objects){
      for (Map<String, Object> obj : objects)
        insert(obj);
    }

    public void select() {
      collection.find();
    }

    public void mapReduce(String map , String reduce, Map<String, Object> scope, String outputTarget) {
      MapReduceCommand command = new MapReduceCommand(collection, map, reduce, outputTarget, MapReduceCommand.OutputType.REDUCE, null);
      command.setScope(scope);
      collection.mapReduce(command);
    }
  }

  @Override
  public void close() throws IOException {
    if (mongoClient != null)
      mongoClient.close();
  }
}
