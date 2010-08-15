package com.googlecode.pigwt.example1.client.utils;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.pigwt.client.PageGroup;

import java.util.Map;

public class UtilsGroup extends Grid implements PageGroup {
    public UtilsGroup() {
        super(2, 1);
        setText(0, 0, "this is a utility...");
    }

    public void show(PageGroup parent, Map<String, String> params) {
        parent.setContent(this);
    }

    public void hide(PageGroup parent) {
        parent.setContent(null);
    }

    public void setContent(Widget content) {
        setWidget(1, 0, content);
    }
}
