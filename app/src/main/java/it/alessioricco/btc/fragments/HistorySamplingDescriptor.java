package it.alessioricco.btc.fragments;

import lombok.Getter;
import lombok.Setter;

/**
 * for each sample contains caching, label, duration
 */
public class HistorySamplingDescriptor {
    private @Getter @Setter String label;
    private @Getter @Setter long cacheDuration;
    private @Getter @Setter long sample;
    private @Getter @Setter long duration;

    // if true it will be used, otherwise no
    private @Getter @Setter Boolean enabled;

    HistorySamplingDescriptor(String label,long sample, long duration, long cacheDuration, Boolean enabled){
        this.label = label;
        this.cacheDuration = cacheDuration;
        this.sample = sample;
        this.duration = duration;
        this.enabled = enabled;
    }
}
