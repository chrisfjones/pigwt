package com.googlecode.pigwt.client;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Map;

public final class RootPanelPageGroup implements PageGroup {
    public void setContent(Widget content) {
        RootPanel.get().add(content);
    }

    public void show(PageGroup parent, Map<String, String> params) {
    }

    public void hide(PageGroup parent) {
    }
}
