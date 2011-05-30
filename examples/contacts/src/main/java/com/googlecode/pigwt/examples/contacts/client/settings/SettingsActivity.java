package com.googlecode.pigwt.examples.contacts.client.settings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class SettingsActivity extends AbstractActivity {
    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(new Label("settings page"));
    }
}
