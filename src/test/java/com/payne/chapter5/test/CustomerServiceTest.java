package com.payne.chapter5.test;

import com.payne.chapter5.help.DatabaseHelper;
import com.payne.chapter5.model.Customer;
import com.payne.chapter5.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dengpeng on 2017/6/19.
 */
public class CustomerServiceTest {
    private final CustomerService customerService;

    public CustomerServiceTest() {
        customerService = new CustomerService();
    }

    //每个test执行后，在测试后数据库里的数据无法自动还原成初始状态
    @Before
    public void init() throws Exception{
        String file = "sql/customer_init.sql";
        DatabaseHelper.executeSqlFile(file);
    }

    @Test
    public void getCustomerListTest(){
        Assert.assertEquals(customerService.getCustomerList().size(),2);
    }

    @Test
    public void getCustomerByIdTest(){
        Customer customer = customerService.getCustomerById(1);
        Assert.assertNotNull(customer);
    }

    @Test
    public void createCustomerTest(){
        Map<String,Object> fieldMap = new HashMap<String,Object>();
        fieldMap.put("name","customer111");
        fieldMap.put("contact","sb");
        fieldMap.put("telephone","13838384388");
        Assert.assertTrue(customerService.createCustomer(fieldMap));
    }

    @Test
    public void updateCustomerTest(){
        Map<String,Object> fieldMap = new HashMap<String,Object>();
        fieldMap.put("name","customer111");
        Assert.assertTrue(customerService.updateCustomer(1,fieldMap));
    }

    @Test
    public void deleteCustomerTest(){
        Assert.assertTrue(customerService.deleteCustomer(1));
    }
}
