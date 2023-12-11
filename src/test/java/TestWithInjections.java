package test.java;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.google.inject.Guice;
import com.google.inject.Injector;

import main.java.common.CommonModule;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class TestWithInjections {

    protected Injector injector = Guice.createInjector(
            new CommonModule());

    @BeforeAll
    public void setupInjections() {
        injector.injectMembers(this);
    }

}
