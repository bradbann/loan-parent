package org.songbai.loan.user.finance.service;

import org.songbai.loan.model.finance.FinanceIOModel;

public interface PayNotifyService {
    void paySuccess(FinanceIOModel ioModel);

    void payFail(FinanceIOModel ioModel, String msg);
}
