package rbt.reports.impl;

import com.mongodb.*;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Реализация логики работы с базой MongoDB
 */
@Service
@Scope("singleton")
public class Mongo implements Closeable {
  // подключение к серверу Mongo
  private MongoClient mongoClient;

  // подключение к БД
  private DB db;

  // тут мы будем хранить состояние подключения к БД
  private boolean authenticate;

  public Mongo() {
    this("localhost", 27017, "test", null, null);
  }

  public Mongo(Properties prop) {
    this(prop.getProperty("host"), Integer.valueOf(prop.getProperty("port")), prop.getProperty("dbname"),
         prop.getProperty("login"), prop.getProperty("password"));
  }

  protected Mongo(String host, Integer port, String dbName, String login, String password) {
    try {
      mongoClient = new MongoClient(host, port); // Создаем подключение
      db = mongoClient.getDB(dbName); // Выбираем БД для работы
      // Входим под созданным логином и паролем
      if (login != null)
        authenticate = db.authenticate(login, password.toCharArray());
    } catch (UnknownHostException e) {
      System.err.println("Don't connect!");
    }
  }

  public class Collection {
    private DBCollection collection;

    public Collection(String collectionName) {
      collection = db.getCollection(collectionName);
    }

    protected Collection(DBCollection collection) {
      this.collection = collection;
    }

    public void insert(Map<String, Object> obj){
      BasicDBObject document = new BasicDBObject(obj);
      collection.insert(document);
    }

    public void insertAll(java.util.Collection<Map<String, Object>> objects){
      for (Map<String, Object> obj : objects)
        insert(obj);
    }

    public java.util.Collection<Map<String, Object>> select() {
      return select(new HashMap<String, Object>());
    }

    public java.util.Collection<Map<String, Object>> select(Map<String, Object> filter) {
      java.util.Collection<Map<String, Object>> result = new ArrayList<>();
      DBCursor cursor = collection.find(new BasicDBObject(filter));
      while(cursor.hasNext()) {
        DBObject obj = cursor.next();
        result.add(obj.toMap());
      }
      return result;
    }

    public Collection mapReduce(String map , String reduce, Map<String, Object> scope, String outputTarget) {
      MapReduceCommand command = new MapReduceCommand(collection, map, reduce, outputTarget, MapReduceCommand.OutputType.REPLACE, null);
      command.setScope(scope);
      return new Collection(collection.mapReduce(command).getOutputCollection());
    }
  }

  @Override
  public void close() throws IOException {
    if (mongoClient != null)
      mongoClient.close();
  }
}
