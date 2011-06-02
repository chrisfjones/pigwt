package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.user.client.ui.*;
import com.googlecode.pigwt.client.Shell;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class MainShell extends VerticalPanel implements Shell {
    private SimplePanel simplePanel;

    @Inject
    public MainShell(final Service service) {
        this.add(new Label("I'm an injected shell... see! --> " + service.getRootMessage()));
        simplePanel = new SimplePanel();
        this.add(simplePanel);
    }

    @Override
    public void setWidget(final IsWidget w) {
        simplePanel.setWidget(w);
    }
}
