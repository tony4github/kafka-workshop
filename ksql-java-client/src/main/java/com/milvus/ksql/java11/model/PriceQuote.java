package com.milvus.ksql.java11.model;

public class PriceQuote {
    private String cusip;
    private String latestPrice;

    public String getCusip() {
        return cusip;
    }
    public void setCusip(String cusip) {
        this.cusip = cusip;
    }
    public String getLatestPrice() {
        return latestPrice;
    }
    public void setLatestPrice(String latestPrice) {
        this.latestPrice = latestPrice;
    }

}
