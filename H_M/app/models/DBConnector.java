package models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;

import play.mvc.Controller;
import play.db.NamedDatabase;
import play.db.Database;

import play.db.*;

import javax.inject.*;
import javax.sql.DataSource;


@Singleton
public class DBConnector {

  private Database db;
  private DatabaseExecutionContext executionContext;

  @Inject
  public DBConnector(@NamedDatabase("homing") Database db, DatabaseExecutionContext executionContext) {
    this.db = db;
    this.executionContext = executionContext;
  }


  public CompletionStage<Connection> getConnection() {
    return CompletableFuture.supplyAsync(
        () -> {
          return db.withConnection(
              connection -> {
                return connection;
              });
        },
        executionContext);
  }

  public DataSource getDataSource() {
    return db.getDataSource();
  }

  public CompletionStage<Void> insert(String IdValue, String StringValue) {
    return CompletableFuture.runAsync(
        () -> {
          // get jdbc connection
          Connection connection = db.getConnection();

          PreparedStatement pst = null;
          ResultSet rs = null;

          try {
            pst = connection
              .prepareStatement("insert into Test (ID, Value) values(?,?);");

            pst.setString(1, IdValue);
            pst.setString(2, StringValue);

            pst.executeUpdate();

          }
          catch(Exception exp) {
            exp.printStackTrace();
          }

          return;
        },
        executionContext);
  }

  public CompletionStage<Void> readAll() {
    System.out.println("Inside readAll");
    return CompletableFuture.runAsync(
        () -> {

          System.out.println("Inside runAsync");
          System.out.println("Inside runAsync db: "+db);
          System.out.println("Inside getDataSource: "+db.getDataSource());
          System.out.println("Inside getName: "+db.getName());

          // get jdbc connection
          Connection connection = db.getConnection();
          System.out.println("Connection : " + connection);

          PreparedStatement pst = null;
          ResultSet rs = null;

          try {
            System.out.println("Inside try");
            pst = connection
              .prepareStatement("select * from Test;");

            rs = pst.executeQuery();
            while(rs.next()) {
              System.out.println("Printing DB Values");
              System.out.println(rs.getString(1) + ":" + rs.getString(2));
            }
            

          }
          catch(Exception exp) {
            System.out.println("Inside catch");
            exp.printStackTrace();
          }

          return;
        },
        executionContext);
  }

  public CompletionStage<Integer> read() {
    System.out.println("Inside read");
    return CompletableFuture.supplyAsync(
        () -> {
          return db.withConnection(
              connection -> {
                System.out.println(connection);
                return 1;
              });
        },
        executionContext);
  }

}