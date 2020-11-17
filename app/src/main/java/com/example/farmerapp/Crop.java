package com.example.farmerapp;

public class Crop {
    private String commodity;
    private String variety;
    private String arrival_date;
    private String min_price;
    private String max_price;
    private String modal_price;

    public Crop(String commodity, String variety, String arrival_date, String min_price, String max_price, String modal_price) {
        this.commodity = commodity;
        this.variety = variety;
        this.arrival_date = arrival_date;
        this.min_price = min_price;
        this.max_price = max_price;
        this.modal_price = modal_price;
    }

    public String getCommodity() {
        return commodity;
    }

    public void setCommodity(String commodity) {
        this.commodity = commodity;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getArrival_date() {
        return arrival_date;
    }

    public void setArrival_date(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public String getMin_price() {
        return min_price;
    }

    public void setMin_price(String min_price) {
        this.min_price = min_price;
    }

    public String getMax_price() {
        return max_price;
    }

    public void setMax_price(String max_price) {
        this.max_price = max_price;
    }

    public String getModal_price() {
        return modal_price;
    }

    public void setModal_price(String modal_price) {
        this.modal_price = modal_price;
    }
}
