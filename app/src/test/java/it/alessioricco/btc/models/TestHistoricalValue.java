package it.alessioricco.btc.models;

/**
 * Created by alessioricco on 02/11/2016.
 */

import org.junit.Test;
import javax.inject.Inject;
import javax.inject.Named;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import it.alessioricco.btc.injection.ObjectGraphSingleton;
import it.alessioricco.btc.injection.TestObjectGraphInitializer;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;

public class TestHistoricalValue {

    @Before public void init() throws Exception {

        // Init the IoC and inject us
        TestObjectGraphInitializer.init();
        TestObjectGraphInitializer.getObjectGraphIstance().inject(this);

    }

    /**
     * Method executed after any test
     */
    @After public void tearDown() {
        TestObjectGraphInitializer.reset();
    }

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

}
