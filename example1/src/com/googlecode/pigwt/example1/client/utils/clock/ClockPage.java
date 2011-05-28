package com.googlecode.pigwt.example1.client.utils.clock;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.pigwt.client.Page;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class ClockPage extends Label implements Page {
    public ClockPage() {
        super("clock page");
    }

    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    public void hide(PageGroup parent) {
        parent.setContent(null);
    }
}
