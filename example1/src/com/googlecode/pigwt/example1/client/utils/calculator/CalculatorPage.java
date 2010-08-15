package com.googlecode.pigwt.example1.client.utils.calculator;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.googlecode.pigwt.client.Page;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class CalculatorPage extends Label implements Page {
    public CalculatorPage() {
        super("calculate some stuff");
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
