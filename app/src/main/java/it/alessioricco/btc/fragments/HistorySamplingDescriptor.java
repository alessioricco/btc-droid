package it.alessioricco.btc.fragments;

import lombok.Getter;
import lombok.Setter;

/**
 * for each sample contains caching, label, duration
 */
public final class HistorySamplingDescriptor {
    private @Getter  String label;
    private @Getter  long cacheDuration;
    private @Getter  long sample;
    private @Getter  long duration;

    // if true it will be used, otherwise no
    private @Getter  Boolean enabled;

    HistorySamplingDescriptor(String label, long sample, long duration, long cacheDuration, Boolean enabled){
        this.label = label;
        this.cacheDuration = cacheDuration;
        this.sample = sample;
        this.duration = duration;
        this.enabled = enabled;
    }
}
