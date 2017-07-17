package com.payne.chapter5.controller;

import com.payne.chapter5.model.Customer;
import com.payne.chapter5.service.CustomerService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by dengpeng on 2017/6/20.
 */
@WebServlet("/customer")
public class CustomerServlet extends HttpServlet{
    private CustomerService customerService;

    @Override
    public void init() throws ServletException {
        customerService = new CustomerService();//可用单例模式优化
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Customer> customerList = customerService.getCustomerList();
        req.setAttribute("customerList",customerList);
        req.getRequestDispatcher("/WEB-INF/view/customer.jsp").forward(req,resp);
    }
}
