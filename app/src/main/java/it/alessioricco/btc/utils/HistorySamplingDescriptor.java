package it.alessioricco.btc.utils;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 01/11/2016.
 */

/**
 * for each sample contains caching, label, duration
 */
public class HistorySamplingDescriptor {
    @Getter @Setter String label;
    @Getter @Setter long cacheDuration;
    @Getter @Setter long sample;
    @Getter @Setter long duration;

    HistorySamplingDescriptor(String label,long sample, long duration, long cacheDuration){
        this.label = label;
        this.cacheDuration = sample;
        this.sample = duration;
        this.duration = cacheDuration;
    }
}
