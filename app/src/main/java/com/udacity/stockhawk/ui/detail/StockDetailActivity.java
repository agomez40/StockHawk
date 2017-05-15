package com.udacity.stockhawk.ui.detail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

/**
 * A detail view for each Stock, shows a graph using the stock's value over time.
 *
 * @author Luis Alberto Gómez Rodríguez (lagomez40@gmail.com)
 * @version 1.0.0 2017/05/15
 * @see AppCompatActivity
 * @since 1.0.0 2017/05/15
 */
public class StockDetailActivity extends AppCompatActivity {

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_detail);
    }
}
