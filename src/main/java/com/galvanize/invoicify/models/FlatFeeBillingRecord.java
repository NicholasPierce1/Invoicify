package com.galvanize.invoicify.models;

public final class FlatFeeBillingRecord extends BillingRecord {

    // fields
    private double amount;

    // constructors

    public FlatFeeBillingRecord(){

    }

    // gets & sets

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    // methods

    @Override
    public double getTotal() {
        return this.getAmount();
    }

}
