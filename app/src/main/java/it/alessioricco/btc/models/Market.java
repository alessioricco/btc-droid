package it.alessioricco.btc.models;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 02/10/2016.
 *
 * this model represent the ticker for a single market
 * {
 * "volume": 295.487194180000,
 * "latest_trade": 1475341802,
 * "bid": 7859700.000000000000,
 * "high": 7875000.000000000000,
 * "currency": "IDR",
 * "currency_volume": 2317540288.579675000000,
 * "ask": 7859800.000000000000,
 * "close": 7859700.000000000000,
 * "avg": 7843115.824396485187810313926,
 * "symbol": "btcoidIDR",
 * "low": 7793200.000000000000}
 */

public class Market implements Serializable {

    @Getter @Setter private Double volume;
    @Getter @Setter private long latest_trade;
    @Getter @Setter private Double bid;
    @Getter @Setter private Double high;
    @Getter @Setter private String currency;
    @Getter @Setter private Double currency_volume;
    @Getter @Setter private Double ask;
    @Getter @Setter private Double close;
    @Getter @Setter private Double avg;
    @Getter @Setter private String symbol;
    @Getter @Setter private Double low;
}
