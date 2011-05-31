package com.googlecode.pigwt.examples.bigapp.client;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;

public class RootActivity extends AbstractActivity {
    private Service service;

//    @Inject
//    public RootActivity(final Service service) {
//        this.service = service;
//    }
    @Inject
    public RootActivity() {
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
//        panel.setWidget(new Label(service.getRootMessage()));
        panel.setWidget(new Label("I'm not injected yet :("));
    }
}
