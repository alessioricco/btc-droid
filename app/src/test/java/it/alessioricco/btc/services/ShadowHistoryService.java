package it.alessioricco.btc.services;

import java.util.Date;

import it.alessioricco.btc.fragments.HistorySample;
import it.alessioricco.btc.models.HistoricalValue;
import rx.Subscriber;

public class ShadowHistoryService extends HistoryService {

    @Override
    protected void HistoryCall(final Subscriber<? super HistoricalValue> subscriber, final HistorySample sample) {
        HistoricalValue history = new HistoricalValue();
        history.setDate(new Date());
        history.setIndex(sample.getIndex());
        history.setValue(10d);
        subscriber.onNext(history);
        subscriber.onCompleted();
    }


}
