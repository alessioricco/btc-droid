package it.alessioricco.btc.util;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.internal.bytecode.InstrumentationConfiguration;


/**
 * Custom Robolectric Runner, useful for custom actions
 */
public class CustomRobolectricTestRunner extends RobolectricTestRunner {
    /**
     * Creates a runner to run {@code testClass}. Looks in your working directory for your AndroidManifest.xml file
     * and res directory by default. Use the {@link Config} annotation to configure.
     *
     * @param testClass the test class to be run
     * @throws InitializationError if junit says so
     */
    public CustomRobolectricTestRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
    }

//    //http://stackoverflow.com/questions/31920865/how-to-create-custom-shadows-in-robolectric-3-0
//    //TODO: use mockito...
    public InstrumentationConfiguration createClassLoaderConfig() {
        InstrumentationConfiguration.Builder builder = InstrumentationConfiguration.newBuilder();
        //todo: check if this line is needed
        builder.addInstrumentedPackage("it.alessioricco.btc.api.RestAdapterFactory");
        return builder.build();
    }
}

