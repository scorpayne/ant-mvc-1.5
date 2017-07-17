package com.payne.chapter5.controller;

import com.payne.chapter5.model.Customer;
import com.payne.chapter5.service.CustomerService;
import org.codehaus.jackson.map.annotate.JacksonInject;

import javax.xml.ws.Action;
import java.util.List;
import java.util.Map;

/**
 * 控制器类，包含若干action方法，每个方法都有get/post/put/delete，用于指定请求类型与请求路径
 * 使用param封装所有请求参数，使用view封装一个jsp页面，使用data封装一个json数据
 * Created by dengpeng on 2017/6/20.
 */
@Controller
public class CustomerController {
    @Inject
    private CustomerService customerService;

    /**
     * 进入客户列表页面
     */
    @Action("get:/customer")
    public View index(){
        List<Customer> customerList = customerService.getCustomerList();
        return new View("customer.jsp").addModel("customerList",customerList);
    }

    /**
     * 显示客户基本信息  getById
     */
    @Action("get:/customer_show")
    public View show(Param param){
        long id = param.getLong("id");
        Customer customer = customerService.getCustomerById(id);
        return new View("customer_show.jsp").addModel("customer",customer);
    }

    /**
     * 进入创建客户页面
     */
    @Action("get:/customer_create")
    public View create(Param param){
        return new View("customer_create.jsp");
    }

    /**
     * 处理创建客户请求
     */
    @Action("post:/customer_create")
    public Data createSubmit(Param param){
        Map<String,Object> fieldMap =  param.getMap();
        //5.0处理请求参数
        FileParm fileParm = param.getFile("photo");

//        boolean result = customerService.createCustomer(fieldMap);
        boolean result = customerService.createCustomer(fieldMap,fileParm);

        return new Data(result);
    }

    /**
     * 进入编辑页面
     */
    @Action("get:/customer_edit")
    public View edit(Param param){
        long id = param.getLong("id");
        Customer customer = customerService.getCustomerById(id);
        return new View("customer_edit.jsp").addModel("customer",customer);
    }

    /**
     * 处理编辑客户请求
     */
    @Action("put:/customer_edit")
    public Data editSubmit(Param param){
        long id = param.getLong("id");
        Map<String,Object> fieldMap =  param.getMap();
        boolean result = customerService.updateCustomer(id, fieldMap);
        return new Data(result);
    }

    /**
     * 处理删除客户请求
     */
    @Action("delete:/customer_delete")
    public Data delete(Param param){
        long id = param.getLong("id");
        boolean result = customerService.deleteCustomer(id);
        return new Data(result);
    }
}
