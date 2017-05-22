package com.udacity.stockhawk.data;

/**
 * Simple POJO to wrap a History pair data
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/22
 * @since 1.0.0 2017/05/22
 */
public class HistoryEntry {
    private Long stockTime;
    private Float stockPrice;

    /**
     * Constructor
     *
     *  @since 1.0.0 2017/05/22
     */
    public HistoryEntry() {
    }

    public Long getStockTime() {
        return stockTime;
    }

    public void setStockTime(Long stockTime) {
        this.stockTime = stockTime;
    }

    public Float getStockPrice() {
        return stockPrice;
    }

    public void setStockPrice(Float stockPrice) {
        this.stockPrice = stockPrice;
    }
}
