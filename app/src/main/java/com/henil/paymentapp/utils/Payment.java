package com.henil.paymentapp.utils;

public class Payment {
    private double amount;
    private TransferTypes type;
    private String Provider = "";
    private String TransactionRef = "";

    public Payment() {
    }

    public TransferTypes getType() {
        return type;
    }

    public void setType(TransferTypes type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String provider) {
        Provider = provider;
    }

    public String getTransactionRef() {
        return TransactionRef;
    }

    public void setTransactionRef(String transactionRef) {
        TransactionRef = transactionRef;
    }

    public String getPaymentType() {
        return type.name();
    }
}

