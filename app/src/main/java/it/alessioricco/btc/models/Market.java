package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.Date;

import it.alessioricco.btc.utils.StringUtils;
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
    @Getter @Setter private Long latest_trade;
    @Getter @Setter private Double bid;
    @Getter @Setter private Double high;
    @Getter @Setter private String currency;
    @Getter @Setter private Double currency_volume;
    @Getter @Setter private Double ask;
    @Getter @Setter private Double close;
    @Getter @Setter private Double avg;
    @Getter @Setter private String symbol;
    @Getter @Setter private Double low;

    public Date getDate() {
        if (latest_trade == null) return null;
        final long dv = latest_trade *1000;
        return new java.util.Date(dv);
    }

    private boolean tooOld() {
        long week = 2L*24L*60L*60L*1000L;
        Date lastWeek = new java.util.Date((new java.util.Date()).getTime() - week);
        return getDate().before(lastWeek);
    }

    public Double percent() {
        //TODO: add a method nearzero abs()<epsilon
        if (avg == null) return null;
        if (avg == 0d) return null;
        Double delta = delta();
        if (delta == null) return null;
        return 100*(delta/avg);
    }

    public Double delta() {
        if (avg == null) return null;
        if (close == null) return null;
        return (close-avg);
    }

    private boolean marketToFilter() {

        if (StringUtils.isNullOrEmpty(symbol)) {
            return true;
        }

        final String uSymbol = symbol.toUpperCase();

        return uSymbol.startsWith("LOCALBTC") ||
                uSymbol.startsWith("BITBAY") ||
                uSymbol.startsWith("BITCUREX");
    }

    /**
     * check if the market is valid and still active
     * @return
     */
    final public boolean isValid() {
        return bid != null &&
                ask != null &&
                high != null &&
                low != null &&
                currency_volume != null &&
                close != null &&
                avg != null &&
                currency_volume > 1 &&
                latest_trade != null &&
                !StringUtils.isNullOrEmpty(symbol) &&
                !tooOld() &&
                !marketToFilter() &&
                !StringUtils.isNullOrEmpty(symbol) &&
                !StringUtils.isNullOrEmpty(currency);
    }
}
