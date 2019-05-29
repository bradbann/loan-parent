package org.songbai.loan.common.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

@Component
public class TransactionHelper {

    @Autowired
    private DataSourceTransactionManager transactionManager;


    public void tx(int behavior, Process process) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        def.setPropagationBehavior(behavior);

        TransactionStatus status = transactionManager.getTransaction(def);

        tx(process, status);
    }

    public void tx(Process process) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        TransactionStatus status = transactionManager.getTransaction(def);
        tx(process, status);
    }


    private void tx(Process process, TransactionStatus status) {
        try {
            process.process();
            transactionManager.commit(status);
        } catch (Throwable e) {
            transactionManager.rollback(status);
            throw new RuntimeException(e);
        }
    }


    @FunctionalInterface
    public interface Process {

        void process();
    }
}
