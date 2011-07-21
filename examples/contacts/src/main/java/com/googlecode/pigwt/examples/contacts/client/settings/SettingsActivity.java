package com.googlecode.pigwt.examples.contacts.client.settings;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;

public class SettingsActivity extends AbstractActivity {
    private Label w;

    public SettingsActivity() {
        w = new Label("settings page");
    }

    @Override
    public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
        panel.setWidget(w);
    }

    @Override
    public String mayStop() {
        return "navigating away from this page will likely cause worldwide panic. proceed?";
    }
}
