package me.joelsoep.ctf.game.task;

import me.joelsoep.ctf.economy.TransactionManager;
import me.joelsoep.ctf.logger.LogModule;

public class ResumePendingTransactionsTask implements Runnable {

    @Override
    public void run() {
        LogModule.ECONOMY.log("Scanning for pending transactions...");
        TransactionManager.resumePendingTransactions();
    }
}
