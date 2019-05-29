package org.songbai.loan.admin.finance.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.finance.service.FinanceDeductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FinanceDeductServiceImplTest {

    @Autowired
    FinanceDeductService financeDeductService;

    @Test
    public void saveFinanceDeductModel() {

        AdminUserModel actorModel = new AdminUserModel();
        actorModel.setId(0);
        actorModel.setName("test");
        financeDeductService.saveFinanceDeductModel(Arrays.asList("L18122717224QWE495"),18,actorModel);
    }
}