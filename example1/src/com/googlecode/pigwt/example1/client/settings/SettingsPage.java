package com.googlecode.pigwt.example1.client.settings;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.pigwt.client.Page;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class SettingsPage extends Label implements Page {
    public SettingsPage() {
        super("I M TEH SAYTINZ PAYJ");
    }

    public void show(PageGroup parent, Map<String, String> params) {
//        parent.setContent(this);
        RootPanel.get().add(this);
    }

    public void hide(PageGroup parent) {
//        parent.setContent(null);
        RootPanel.get().clear();
    }
}
