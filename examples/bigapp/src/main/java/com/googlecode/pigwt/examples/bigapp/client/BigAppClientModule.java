package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.googlecode.pigwt.examples.bigapp.client.attributes.AttributesActivity;
import com.googlecode.pigwt.examples.bigapp.client.attributes.AttributesView;

public class BigAppClientModule extends AbstractGinModule {
    @Override
    protected void configure() {
        bind(AttributesActivity.View.class).to(AttributesView.class);
    }
}
