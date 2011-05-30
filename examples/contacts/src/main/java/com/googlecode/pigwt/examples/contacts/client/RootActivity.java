package com.googlecode.pigwt.examples.contacts.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class RootActivity extends AbstractActivity {
    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        // initialize stuff
        panel.setWidget(new Label("home page"));
    }
}
