package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

@GinModules(BigAppClientModule.class)
public interface BigAppInjector extends Ginjector {
    Service getService();
}
