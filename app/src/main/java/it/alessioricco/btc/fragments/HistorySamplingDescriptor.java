package it.alessioricco.btc.fragments;

import lombok.Getter;

/**
 * for each sample contains caching, label, duration
 */
public final class HistorySamplingDescriptor {
    final private @Getter  String label;
    final private @Getter  long cacheDuration;
    final private @Getter  long sample;
    final private @Getter  long duration;

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
