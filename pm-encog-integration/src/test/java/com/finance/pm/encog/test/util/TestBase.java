package com.finance.pm.encog.test.util;

import org.junit.Before;

import com.finance.pm.encog.application.EncogService;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class TestBase {

    protected Injector injector = Guice.createInjector(new AbstractModule() {
        @Override
        protected void configure() {
            bind(EncogService.class);
        }
    });

    @Before
    public void setup() {
        injector.injectMembers(this);
    }
}