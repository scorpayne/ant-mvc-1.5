package com.payne.chapter5.service;

import com.payne.chapter5.help.DatabaseHelper;
import com.payne.chapter5.model.Customer;
import com.payne.chapter5.util.PropsUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dengpeng on 2017/6/19.
 */
public class CustomerService {
    private static final String DRIVER;
    private static final String URL;
    private static final String USERNAME;
    private static final String PASSWORD;

    static {
        Properties properties = PropsUtil.loadProps("config.properties");
        DRIVER = properties.getProperty("jdbc.driver");
        URL = properties.getProperty("jdbc.url");
        USERNAME = properties.getProperty("jdbc.username");
        PASSWORD = properties.getProperty("jdbc.password");

        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //获取客户列表
    public List<Customer> getCustomerList() {
        List<Customer> customerList = new ArrayList<>();
        /*Connection conn = null;

        try {
            String sql = "select * from customer";
            conn = DriverManager.getConnection(
                    URL, USERNAME, PASSWORD
            );
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Customer customer = new Customer();
                customer.setId(resultSet.getLong("id"));
                customer.setContact(resultSet.getString("contact"));
                customer.setEmail(resultSet.getString("email"));
                customer.setName(resultSet.getString("name"));
                customer.setTelephone(resultSet.getString("telephone"));
                customer.setRemark(resultSet.getString("remark"));
                customerList.add(customer);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            if(conn!=null){
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }*/
        String sql = "select * from customer";
        customerList = DatabaseHelper.queryEntityList(Customer.class,sql);

        return customerList;
    }

    /**
     * 获取单个客户，根据id
     */
    public Customer getCustomerById(long id) {
        String sql = "select * from customer where id=?";
        return DatabaseHelper.queryEntity(Customer.class,sql,id);
    }

    /**
     * 创建单个客户
     */
    public boolean createCustomer(Map<String, Object> map) {
        return DatabaseHelper.insertEntity(Customer.class,map);
    }
    /**
     * 创建单个客户，包含上传文件
     */
    public boolean createCustomer(Map<String, Object> map,FileParam fileParam) {
        boolean result = DatabaseHelper.insertEntity(Customer.class, map);
        if(result){
            UploadHelper.uploadFile("/tmp/upload",fileParam);
        }
        return result;
    }

    /**
     * 更新单个客户，根据id
     */
    public boolean updateCustomer(long id, Map<String, Object> map) {
        return DatabaseHelper.updateEntity(Customer.class,id,map);
    }

    /**
     * 删除单个客户
     * @param id
     * @return
     */
    public boolean deleteCustomer(long id){
        return DatabaseHelper.deleteEntity(Customer.class,id);
    }
}
