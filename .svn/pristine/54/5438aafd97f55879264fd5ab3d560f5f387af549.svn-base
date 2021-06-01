package com.mapsengineering.base.util;

import javax.transaction.Transaction;

import org.ofbiz.base.util.Debug;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;

/**
 * Execute a task inside a transaction.
 * @author sivi
 *
 */
public class TransactionRunner {

    private final String module;
    private final boolean requireNewTransaction;
    private final int transactionTimeout;
    private final TransactionItem runnable;

    private Transaction parentTransaction;
    private boolean beganTrans;
    private Exception error;
    private String exceptionMsg = "Error in TransactionRunner: ";

    /**
     * Creates a new runner
     * @param module for log messages
     * @param requireNewTransaction true to suspend current and start a new one
     * @param transactionTimeout in seconds
     * @param runnable task to execute
     */
    public TransactionRunner(String module, boolean requireNewTransaction, int transactionTimeout, TransactionItem runnable) {
        this.module = module != null ? module : TransactionRunner.class.getName();
        this.requireNewTransaction = requireNewTransaction;
        this.transactionTimeout = transactionTimeout;
        this.runnable = runnable;
    }

    /**
     * Creates a new runner
     * @param module for log messages
     * @param requireNewTransaction true to suspend current and start a new one
     * @param runnable task to execute
     */
    public TransactionRunner(String module, boolean requireNewTransaction, TransactionItem runnable) {
        this(module, requireNewTransaction, 0, runnable);
    }

    /**
     * Creates a new runner
     * @param module for log messages
     * @param transactionTimeout in seconds
     * @param runnable task to execute
     */
    public TransactionRunner(String module, int transactionTimeout, TransactionItem runnable) {
        this(module, false, transactionTimeout, runnable);
    }

    /**
     * Creates a new runner
     * @param module for log messages
     * @param runnable task to execute
     */
    public TransactionRunner(String module, TransactionItem runnable) {
        this(module, false, 0, runnable);
    }

    /**
     * Gets the pending exception
     * @return
     */
    public Exception getError() {
        return error;
    }

    /**
     * Execute the task and catch all exceptions
     * @return this
     */
    public TransactionRunner run() {
        try {
            execute();
        } catch (GenericEntityException e) {
            if (error == null) {
                error = e;
            }
        }
        return this;
    }

    /**
     * Execute the task, throws exception on commit or resume.
     * @return this
     * @throws GenericEntityException
     */
    public TransactionRunner execute() throws GenericEntityException {
        error = null;
        parentTransaction = null;
        beganTrans = false;
        try {
            beginTrx();
            runnable.run();
        } catch (Exception e) {
            error = e;
            rollbackTrx(e);
        } finally {
            finishTrx();
        }
        return this;
    }

    /**
     * Rethrows any pending exception.
     * @return this
     * @throws Exception
     */
    public TransactionRunner rethrow() throws Exception {
        if (error != null) {
            throw error;
        }
        return this;
    }

    /**
     * Log pending exception on a JobLogger.
     * @param jLogger
     * @return this
     */
    public TransactionRunner logError(JobLogger jLogger) {
        if (error != null) {
            jLogger.printLogError(error, exceptionMsg);
        }
        return this;
    }

    protected void beginTrx() throws GenericTransactionException {
        // Try to suspend and use a new transaction.
        if (TransactionUtil.isTransactionInPlace()) {
            if (requireNewTransaction) {
                parentTransaction = TransactionUtil.suspend();
                if (TransactionUtil.isTransactionInPlace()) {
                    Debug.logWarning("Transaction is still in place after suspend, status is " + TransactionUtil.getStatusString(), module);
                } else {
                    beganTrans = TransactionUtil.begin(transactionTimeout);
                }
            }
        } else {
            beganTrans = TransactionUtil.begin(transactionTimeout);
        }
    }

    protected void rollbackTrx(Exception e) {
        String errMsg = exceptionMsg;
        Debug.logError(e, errMsg, module);
        try {
            TransactionUtil.rollback(beganTrans, errMsg, e);
        } catch (GenericTransactionException et) {
            Debug.logError(et, "Could not rollback transaction: " + et.toString(), module);
        }
    }

    protected void finishTrx() throws GenericEntityException {
        try {
            TransactionUtil.commit(beganTrans);
        } catch (GenericTransactionException e) {
            String errMsg = "Could not commit transaction";
            Debug.logError(e, errMsg, module);
            if (e.getMessage() != null) {
                errMsg = errMsg + ": " + e.getMessage();
            }
            throw new GenericEntityException(errMsg, e);
        }
        if (parentTransaction != null) {
            try {
                TransactionUtil.resume(parentTransaction);
            } catch (GenericTransactionException ite) {
                Debug.logWarning(ite, "Transaction error, not resumed", module);
                throw new GenericEntityException("Resume transaction exception, see logs", ite);
            }
        }
    }
}
