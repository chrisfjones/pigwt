package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class RootActivity extends AbstractActivity {
    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(new Label("hi"));
    }
}
