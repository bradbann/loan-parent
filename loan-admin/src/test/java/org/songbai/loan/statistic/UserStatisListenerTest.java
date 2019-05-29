package org.songbai.loan.statistic;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.model.loan.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/20 8:10 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserStatisListenerTest {
    @Autowired
    OrderDao orderDao;

    @Test
    public void test() {
        String orderNumber = "11541145967150485,115411459671504855";
        List<String> orderNumbers = Arrays.asList(orderNumber.split(","));
        List<OrderModel> orderList = orderDao.findOrderListByReview(orderNumbers, null);
        System.out.println(orderList);
    }


}