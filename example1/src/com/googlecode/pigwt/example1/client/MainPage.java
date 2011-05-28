package com.googlecode.pigwt.example1.client;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.pigwt.client.Page;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class MainPage extends Label implements Page {
    public MainPage() {
        super("MAIN PAGE");
    }

    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    public void hide(PageGroup parent) {
        parent.setContent(null);
    }
}
