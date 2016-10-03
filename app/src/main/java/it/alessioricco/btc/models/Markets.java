package it.alessioricco.btc.models;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by alessioricco on 01/10/2016.
 *
 * this model represent the available markets
 * is an array of Market objects
 */
public class Markets implements Serializable {

    @Getter @Setter private List<Market> markets;

}

