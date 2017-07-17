package com.payne.chapter5.help;

import com.payne.chapter5.util.PropsUtil;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dengpeng on 2017/6/19.
 */
public class DatabaseHelper {
/*    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;*/

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseHelper.class);
    /*DbUtils提供的query_runner对象可以面向实体进行查询，他实际上先执行sql返回一个resultSet，然后通过反射去创建并初始化实体对象。*/
    private static final QueryRunner QUERY_RUNNER;
    /*为了确保线程一个线程里只有一个Connection，我们可以用ThreadLocal来存放本地线程变量，ThreadLocal可以理解为一个隔离线程的容器*/
    private static final ThreadLocal<Connection> CONNECTION_HOLDER ;

    /*考虑到每次执行crud都要getConnection，频繁创建数据库连接，造成大的系统开销，所以使用apache dbcp。用"数据库连接池"来管理*/
    private static final BasicDataSource DATA_SOURCE;


    /**
     * BeanHandler —— 返回bean对象
     * BeanListHandler——返回List对象
     */



    static {
        CONNECTION_HOLDER = new ThreadLocal<>();
        QUERY_RUNNER= new QueryRunner();

        Properties properties = PropsUtil.loadProps("config.properties");

/*      1.0
       DRIVER = properties.getProperty("jdbc.driver");
        URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");*/
        //1.1
        String driver = properties.getProperty("jdbc.driver");
        String url = properties.getProperty("jdbc.url");
        String username = properties.getProperty("jdbc.username");
        String password = properties.getProperty("jdbc.password");


        DATA_SOURCE = new BasicDataSource();

        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);


/*        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver",e);
        }*/
    }

    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
//        Connection conn = null;
        Connection conn = CONNECTION_HOLDER.get();
        if(conn == null){
            try {
/* 1.0               conn = DriverManager.getConnection(
                        URL, USERNAME, PASSWORD
                );*/
                conn = DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                LOGGER.error("get connection failure",e);
                throw new RuntimeException(e);
            }
            finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }

/*  1.0
   public static void closeConnection(Connection conn){
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
            }
        }
    }*/

//1.1  删除关闭连接方法
    /*public static void cloaseConnection(){
        Connection conn = CONNECTION_HOLDER.get();
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure",e);
                throw new RuntimeException(e);
            }finally{
                CONNECTION_HOLDER.remove();
            }
        }
    }*/

    /**
     *
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static<T> List<T> queryEntityList(Class<T> entityClass, String sql, Object...params){

        List<T> entityList;
        try{
            Connection conn = getConnection();
            entityList = QUERY_RUNNER.query(conn, sql, new BeanListHandler<T>(entityClass), params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        //1.1
        /*finally{
            cloaseConnection(); //保证查询完之后关闭
        }*/
        return entityList;
    }

    /**
     * 查询单个实体
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static<T> T queryEntity(Class<T> entityClass,String sql,Object...params){
        T entity;
        try {
            entity = QUERY_RUNNER.query(getConnection(), sql, new BeanHandler<T>(entityClass), params);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("query entity failure",e);
            throw new RuntimeException(e);
        }
        return entity;
    }

    /**
     *  输入一个sql与动态参数，输出一个List对象，主要应对多表查询
     */
    public static List<Map<String,Object>> executeQuery(String sql,Object...params){
        List<Map<String,Object>> result;
        try {
            result = QUERY_RUNNER.query(getConnection(), sql, new MapListHandler(), params);
        } catch (SQLException e) {
            e.printStackTrace();
            LOGGER.error("execute query failure",e);
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 接下来是通用的 update insert delete
     */
    public static int executeUpdate(String sql,Object...params){
        int rows =0;
        try{
            rows = QUERY_RUNNER.update(getConnection(), sql, params);
        }catch (SQLException e){
            LOGGER.error("execute update failure",e);
            throw new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 插入实体
     * @param entityClass
     * @param fieldMap
     * @return
     */
    public static<T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
        if(fieldMap == null || fieldMap.isEmpty()){
            LOGGER.error("can not insert entity:field map is empty");
            return false;
        }
        /*getTableName(entityClass)*/
        /*insert into xx (column1,column2,...) values (value1,value2,value3,...);*/
        String sql = "insert into "+getTableName(entityClass)+" ";
        StringBuilder columns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for(String key:fieldMap.keySet()){
            columns.append(key+", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql += columns + " values "+values;

        Object[] params = fieldMap.values().toArray();
        return executeUpdate(sql,params) == 1;
    }

    /**
     * 更新实体
     * @param entityClass
     * @param id
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static<T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
        if(fieldMap == null || fieldMap.isEmpty()){
            LOGGER.error("can not insert entity:field map is empty");
            return false;
        }
        String sql = "update "+getTableName(entityClass)+" set ";
        StringBuilder columns = new StringBuilder();
        for(String key:fieldMap.keySet()){
            columns.append(key).append("=?, ");
        }
        sql += columns.substring(0,columns.lastIndexOf(", ")) + " where id=?";

        List<Object> paramList = new ArrayList<>();
        paramList.addAll(fieldMap.values());
        paramList.add(id);

        return executeUpdate(sql,paramList.toArray())==1;

    }

    public static<T> boolean deleteEntity(Class<T> entityClass,long id){
        String sql = "delete from "+getTableName(entityClass)+" where id =?";
        return executeUpdate(sql,id)==1;
    }

    private static String getTableName(Class<?> entityClass){
        return entityClass.getSimpleName();
    }

    public static void executeSqlFile(String filePath){
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String sql;
        try {
            while ((sql = br.readLine())!=null) {
                DatabaseHelper.executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.error("execute sql file failure",e);
            throw new RuntimeException(e);
        }
    }
}
