package com.galvanize.invoicify.models;

public final class RateBasedBillingRecord extends BillingRecord {

    // fields

    private double rate;

    private double quantity;

    // constructors

    public RateBasedBillingRecord(){

    }

    // gets & sets

    public double getQuantity() {
        return quantity;
    }

    public double getRate() {
        return rate;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    // methods

    @Override
    public double getTotal() {
        return this.getQuantity() * this.getRate();
    }

}
